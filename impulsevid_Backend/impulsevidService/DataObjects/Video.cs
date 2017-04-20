using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Microsoft.Azure.Mobile.Server;
using System.ComponentModel.DataAnnotations;

namespace impulsevidService.DataObjects
{
    public class Video : EntityData
    {
        public string Title { get; set; }
        public string Category { get; set; }
        public string Description { get; set; }
        public bool Available { get; set; }
        public string ProfileID { get; set; }
        public string BlobUrl { get; set; }
        public string StreamUrl { get; set; }
        public string ThumbUrl { get; set; }
    }
}