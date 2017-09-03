using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;
/***************************************************************************************
*    Title: Notifications to WebApp
*    Author: Vivien Chevallier
*    Date: 1/1/17
*    Code version: 1
*    Availability: https://www.vivien-chevallier.com/Articles/sending-push-notifications-in-azure-webjobs-with-azure-notification-hubs-extension
*
***************************************************************************************/
namespace VideoProcess.Extensions
{
    public static class PlatformNotificationsExtensions
    {
        public static string ToGcmPayload(this string message)
        {
            var gcmPayloadModel = new
            {
                data = new
                {
                    message = message
                }
            };

            return JsonConvert.SerializeObject(gcmPayloadModel);
        }
    }
}
