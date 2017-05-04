using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.IO;
namespace impulsevidService.DataObjects
{
    public class VideoBlobInformation
    {
        public Uri BlobUri { get; set; }

        public string BlobName
        {
            get
            {
                return BlobUri.Segments[BlobUri.Segments.Length - 1];
            }
        }
        public string BlobNameWithoutExtension
        {
            get
            {
                return Path.GetFileNameWithoutExtension(BlobName);
            }
        }
        public string ProfileId { get; set; }
        public string VideoId { get; set; }
    }
}