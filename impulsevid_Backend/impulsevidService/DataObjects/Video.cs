using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Microsoft.Azure.Mobile.Server;

namespace impulsevidService.DataObjects
{
    public class Video : EntityData
    {
        public string Title { get; set; }
        public string Category { get; set; }
        public string Description { get; set; }
        public bool Available { get; set; }
        public string Url { get; set; }
        public string ProfileID { get; set; }
        // a video has one user
        public Profile Profile { get; set; }
    }
}