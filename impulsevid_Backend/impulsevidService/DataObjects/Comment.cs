using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Microsoft.Azure.Mobile.Server;
using System.ComponentModel.DataAnnotations;

namespace impulsevidService.DataObjects
{
    public class Comment : EntityData
    {
        public string ProfileID { get; set; }
        public string VideoID { get; set; }
        public string CommentContent { get; set; }
    }
}