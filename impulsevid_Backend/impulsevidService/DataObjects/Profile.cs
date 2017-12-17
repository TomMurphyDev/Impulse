﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Microsoft.Azure.Mobile.Server;
namespace impulsevidService.DataObjects
{
    public class Profile:EntityData
    {

        public string Username { get; set; }
        public string Location { get; set; }
        public string Bio { get; set; }
        public string ProfileUrl { get; set; }
        public string thumbUrl { get; set; }
    }
}