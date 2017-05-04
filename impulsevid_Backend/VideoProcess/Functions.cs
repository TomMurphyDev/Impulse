using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Azure.WebJobs;
using impulsevidService.Models;
using impulsevidService.DataObjects;
using Microsoft.WindowsAzure.Storage.Blob;
using Microsoft.WindowsAzure.MediaServices.Client;
using System.Configuration;
using System.Threading;
using Microsoft.WindowsAzure.Storage.Auth;
using Microsoft.WindowsAzure.Storage;
using System.Globalization;
using System.Net;
using System.Runtime.Serialization.Json;
using System.Web;
using System.Xml;
using Microsoft.WindowsAzure;

namespace VideoProcess
{
    public class Functions
    {
        // This function will get triggered/executed when a new message is written 
        // on an Azure Queue called queue.
        static string accName = "impmedia";
        static string accKey = "pJ7qnzGA/7vusGbMYNWXGeToUkR6bo4W/yg3XeinDDY=";
        static CloudMediaContext context = null;
        private static CloudStorageAccount sourceStorageAccount = null;
        private static CloudStorageAccount destinationStorageAccount = null;
        // Use the cached credentials to create CloudMediaContext.
        //static CloudMediaContext context = new CloudMediaContext(accName,accKey);
        public static void ProcessVideo(
        [QueueTrigger("videorequest")] VideoBlobInformation blobInfo,
        [Blob("{ProfileId}/{BlobName}", FileAccess.Read)] Stream input, TextWriter log)
        {
            VideoBlobInformation b = blobInfo;
            string s = ConvertAndPrepareVideo(b, input,log);

            // Entity Framework context class is not thread-safe, so it must
            // be instantiated and disposed within the function.
            using (impulsevidContext db = new impulsevidContext())
            {
                var id = blobInfo.VideoId;
                Video ad = db.Videos.Find(id);
                if (ad == null)
                {
                    throw new Exception(String.Format("AdId {0} not found, can't create thumbnail", id.ToString()));
                }
                ad.StreamUrl=s;
                db.SaveChanges();
            }
        }
        private static IMediaProcessor GetLatestMediaProcessorByName(string mediaProcessorName)
        {
            var processor = context.MediaProcessors.Where(p => p.Name == mediaProcessorName).
            ToList().OrderBy(p => new Version(p.Version)).LastOrDefault();

            if (processor == null)
                throw new ArgumentException(string.Format("Unknown media processor", mediaProcessorName));
            return processor;
        }
        static public IAsset EncodeToAdaptiveBitrateMP4Set(IAsset asset)
        {
            // Declare a new job.
            IJob job = context.Jobs.Create("Media Encoder Standard Job");
            // Get a media processor reference, and pass to it the name of the 
            // processor to use for the specific task.
            IMediaProcessor processor = GetLatestMediaProcessorByName("Media Encoder Standard");

            // Create a task with the encoding details, using a string preset.
            // In this case "Adaptive Streaming" preset is used.
            ITask task = job.Tasks.AddNew("My encoding task",
                processor,
                "Adaptive Streaming",
                TaskOptions.None);

            // Specify the input asset to be encoded.
            task.InputAssets.Add(asset);
            // Add an output asset to contain the results of the job. 
            // This output is specified as AssetCreationOptions.None, which 
            // means the output asset is not encrypted. 
            task.OutputAssets.AddNew("Output asset",
                AssetCreationOptions.None);

            job.StateChanged += new EventHandler<JobStateChangedEventArgs>(JobStateChanged);
            job.Submit();
            job.GetExecutionProgressTask(CancellationToken.None).Wait();

            return job.OutputMediaAssets[0];
        }

        private static void JobStateChanged(object sender, JobStateChangedEventArgs e)
        {
            Console.WriteLine("Job state changed event:");
            Console.WriteLine("  Previous state: " + e.PreviousState);
            Console.WriteLine("  Current state: " + e.CurrentState);
            switch (e.CurrentState)
            {
                case JobState.Finished:
                    Console.WriteLine();
                    Console.WriteLine("Job is finished. Please wait while local tasks or downloads complete...");
                    break;
                case JobState.Canceling:
                case JobState.Queued:
                case JobState.Scheduled:
                case JobState.Processing:
                    Console.WriteLine("Please wait...\n");
                    break;
                case JobState.Canceled:
                case JobState.Error:

                    // Cast sender as a job.
                    IJob job = (IJob)sender;

                    // Display or log error details as needed.
                    break;
                default:
                    break;
            }
        }
        public static string ConvertAndPrepareVideo(VideoBlobInformation info, Stream input, TextWriter log)
        {
            context = new CloudMediaContext(new MediaServicesCredentials(
                            accName,
                            accKey));
            sourceStorageAccount = new CloudStorageAccount(new StorageCredentials("impstaging", "1BDdeZYFU+DLLrMLaHwcqPcSdzPT20rASvuZZ3wsVWxdq3SGJjZL2Xt4ACiaIiwvRgQfHyiJrz2YFgfGNyaWvg=="), true);
            destinationStorageAccount = new CloudStorageAccount(new StorageCredentials("impstaging", "1BDdeZYFU+DLLrMLaHwcqPcSdzPT20rASvuZZ3wsVWxdq3SGJjZL2Xt4ACiaIiwvRgQfHyiJrz2YFgfGNyaWvg=="), true);
            //output all webjob input blobs into a container -mediacontroller- 
            CloudBlobClient cloudBlobClient = sourceStorageAccount.CreateCloudBlobClient();
            CloudBlobContainer mediaBlobContainer = cloudBlobClient.GetContainerReference("impfile");
            mediaBlobContainer.CreateIfNotExists();
            CloudBlobClient destBlobStorage = destinationStorageAccount.CreateCloudBlobClient();
            // Create a new asset. 
            IAsset asset = context.Assets.Create("TomTest_" + Guid.NewGuid(), AssetCreationOptions.None);
            IAccessPolicy writePolicy = context.AccessPolicies.Create("writePolicy",
                TimeSpan.FromHours(24), AccessPermissions.Write);
            ILocator destinationLocator =
               context.Locators.CreateLocator(LocatorType.Sas, asset, writePolicy);
            // Get the asset container URI and Blob copy from mediaContainer to assetContainer. 
            CloudBlobContainer destAssetContainer =
               destBlobStorage.GetContainerReference((new Uri(destinationLocator.Path)).Segments[1]);
            if (destAssetContainer.CreateIfNotExists())
            {
                destAssetContainer.SetPermissions(new BlobContainerPermissions
                {
                    PublicAccess = BlobContainerPublicAccessType.Blob
                });
            }
            CloudBlobContainer sourceContainer = cloudBlobClient.GetContainerReference(info.ProfileId);
            var blobList = sourceContainer.ListBlobs();
            foreach (var sourceBlob in blobList.Where(b => b.Uri == info.BlobUri))
            {
                log.WriteLine("Found File" + sourceBlob.ToString());
                var assetFile = asset.AssetFiles.Create((sourceBlob as ICloudBlob).Name);
                CloudBlob destinationBlob = destAssetContainer.GetBlockBlobReference(assetFile.Name);
                destAssetContainer.SetPermissions(new BlobContainerPermissions
                {
                    PublicAccess = BlobContainerPublicAccessType.Blob
                });
                // Call the CopyBlobHelpers.CopyBlobAsync extension method to copy blobs.
                destinationBlob.StartCopy(info.BlobUri);
                log.WriteLine("Asset created after copy " + destinationBlob.Uri);
                assetFile.ContentFileSize = (sourceBlob as ICloudBlob).Properties.Length;
                assetFile.Update();
                log.WriteLine("File {0} is of {1} size", assetFile.Name, assetFile.ContentFileSize);
            }
            asset.Update();

            destinationLocator.Delete();
            writePolicy.Delete();

            // Set the primary asset file.
            // If, for example, we copied a set of Smooth Streaming files, 
            // set the .ism file to be the primary file. 
            // If we, for example, copied an .mp4, then the mp4 would be the primary file. 
            var ismAssetFiles = asset.AssetFiles.ToList().
                Where(f => f.Name.EndsWith(".mp4", StringComparison.OrdinalIgnoreCase)).ToArray();
            // The following code assigns the first .ism file as the primary file in the asset.
            // An asset should have one .ism file.  
            ismAssetFiles.First().IsPrimary = true;
            ismAssetFiles.First().Update();
            asset = EncodeToAdaptiveBitrateMP4Set(asset);
            var streamingAssetId = asset.Id;

            var daysForWhichStreamingUrlIsActive = 365;
            var streamingAsset = context.Assets.Where(a => a.Id == streamingAssetId).FirstOrDefault();
            var accessPolicy = context.AccessPolicies.Create(streamingAsset.Name, TimeSpan.FromDays(daysForWhichStreamingUrlIsActive),
                                                     AccessPermissions.Read);
            string streamingUrl = string.Empty;
            var assetFiles = streamingAsset.AssetFiles.ToList();
            var streamingAssetFile = assetFiles.Where(f => f.Name.ToLower().EndsWith("m3u8-aapl.ism")).FirstOrDefault();
            if (streamingAssetFile != null)
            {
                var locator = context.Locators.CreateLocator(LocatorType.OnDemandOrigin, streamingAsset, accessPolicy);
                Uri hlsUri = new Uri(locator.Path + streamingAssetFile.Name + "/manifest(format=m3u8-aapl)");
                streamingUrl = hlsUri.ToString();
                log.WriteLine("Streaming Url Apple : " + streamingUrl);

            }
            streamingAssetFile = assetFiles.Where(f => f.Name.ToLower().EndsWith(".ism")).FirstOrDefault();
            if (string.IsNullOrEmpty(streamingUrl) && streamingAssetFile != null)
            {
                var locator = context.Locators.CreateLocator(LocatorType.OnDemandOrigin, streamingAsset, accessPolicy);
                Uri smoothUri = new Uri(locator.Path + streamingAssetFile.Name + "/manifest");
                streamingUrl = smoothUri.ToString();
                log.WriteLine("Streaming Url SMooth: " + streamingUrl);

            }
            streamingAssetFile = assetFiles.Where(f => f.Name.ToLower().EndsWith(".mp4")).FirstOrDefault();
            if (string.IsNullOrEmpty(streamingUrl) && streamingAssetFile != null)
            {
                var locator = context.Locators.CreateLocator(LocatorType.OnDemandOrigin, streamingAsset, accessPolicy);
                var mp4Uri = new UriBuilder(locator.Path);
                mp4Uri.Path += "/" + streamingAssetFile.Name;
                streamingUrl = mp4Uri.ToString();
                log.WriteLine("Streaming Url:  Url " + streamingUrl);
            }
            log.WriteLine("Streaming Url: " + streamingUrl);
            log.WriteLine("Done");
            return streamingUrl;
        }
    }
}
