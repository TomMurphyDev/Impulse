using System.Linq;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Controllers;
using System.Web.Http.OData;
using Microsoft.Azure.Mobile.Server;
using impulsevidService.DataObjects;
using impulsevidService.Models;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Net;
using System.Web;
using Microsoft.WindowsAzure.Storage.Table.DataServices;
using Microsoft.WindowsAzure.Storage.Blob;
using Microsoft.WindowsAzure.Storage;
using System.IO;
using Microsoft.WindowsAzure.Storage.Queue;
using Microsoft.WindowsAzure.Storage.RetryPolicies;
using System.Diagnostics;
using Microsoft.WindowsAzure;
using System.Configuration;
using Newtonsoft.Json;


namespace impulsevidService.Controllers
{
    public class ProfileController : TableController<Profile>
    {
        private CloudQueue thumbnailRequestQueue;
        private static CloudBlobContainer imagesBlobContainer;
        protected override void Initialize(HttpControllerContext controllerContext)
        {
          
            base.Initialize(controllerContext);
            impulsevidContext context = new impulsevidContext();
            DomainManager = new EntityDomainManager<Profile>(context, Request);
            //// Open storage account using credentials from .cscfg file.
            //var storageAccount = CloudStorageAccount.Parse(ConfigurationManager.ConnectionStrings["AzureWebJobsStorage"].ToString());

            //// Get context object for working with blobs, and 
            //// set a default retry policy appropriate for a web user interface.
            //var blobClient = storageAccount.CreateCloudBlobClient();
            ////blobClient.DefaultRequestOptions.RetryPolicy = new LinearRetry(TimeSpan.FromSeconds(3), 3);

            //// Get a reference to the blob container.
            //imagesBlobContainer = blobClient.GetContainerReference("images");

            //// Get context object for working with queues, and 
            //// set a default retry policy appropriate for a web user interface.
            //CloudQueueClient queueClient = storageAccount.CreateCloudQueueClient();
            ////queueClient.DefaultRequestOptions.RetryPolicy = new LinearRetry(TimeSpan.FromSeconds(3), 3);

            //// Get a reference to the queue.
            //thumbnailRequestQueue = queueClient.GetQueueReference("thumbnailrequest");

        }

        // GET tables/Profile
        public IQueryable<Profile> GetAllProfile()
        {
            return Query(); 
        }

        // GET tables/Profile/48D68C86-6EA6-4C25-AA33-223FC9A27959
        public SingleResult<Profile> GetProfile(string id)
        {
            return Lookup(id);
        }

        // PATCH tables/Profile/48D68C86-6EA6-4C25-AA33-223FC9A27959
        public Task<Profile> PatchProfile(string id, Delta<Profile> patch)
        {
             return UpdateAsync(id, patch);
        }

        // POST tables/Profile
        public async Task<IHttpActionResult> PostProfile(Profile item)
        {
            Profile current = await InsertAsync(item);
            BlobInformation blobInfo = new BlobInformation() { ProfileId = current.Id, BlobUri = new System.Uri(current.ProfileUrl)};
            var queueMessage = new CloudQueueMessage(JsonConvert.SerializeObject(blobInfo));
            await thumbnailRequestQueue.AddMessageAsync(queueMessage);
            return CreatedAtRoute("Tables", new { id = current.Id }, current);
        }

        // DELETE tables/Profile/48D68C86-6EA6-4C25-AA33-223FC9A27959
        public Task DeleteProfile(string id)
        {
             return DeleteAsync(id);
        }
    }
}
