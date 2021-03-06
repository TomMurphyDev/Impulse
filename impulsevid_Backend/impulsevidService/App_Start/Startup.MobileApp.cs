﻿using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data.Entity;
using System.Web.Http;
using Microsoft.Azure.Mobile.Server;
using Microsoft.Azure.Mobile.Server.Authentication;
using Microsoft.Azure.Mobile.Server.Config;
using impulsevidService.DataObjects;
using impulsevidService.Models;
using Owin;

namespace impulsevidService
{
    public partial class Startup
    {
        public static void ConfigureMobileApp(IAppBuilder app)
        {
            HttpConfiguration config = new HttpConfiguration();

            //For more information on Web API tracing, see http://go.microsoft.com/fwlink/?LinkId=620686 
            config.EnableSystemDiagnosticsTracing();

            new MobileAppConfiguration()
                .UseDefaultConfiguration()
                .ApplyTo(config);

            // Use Entity Framework Code First to create database tables based on your DbContext
            Database.SetInitializer(new impulsevidInitializer());

            // To prevent Entity Framework from modifying your database schema, use a null database initializer
            // Database.SetInitializer<impulsevidContext>(null);

            MobileAppSettingsDictionary settings = config.GetMobileAppSettingsProvider().GetMobileAppSettings();

            if (string.IsNullOrEmpty(settings.HostName))
            {
                // This middleware is intended to be used locally for debugging. By default, HostName will
                // only have a value when running in an App Service application.
                app.UseAppServiceAuthentication(new AppServiceAuthenticationOptions
                {
                    SigningKey = ConfigurationManager.AppSettings["SigningKey"],
                    ValidAudiences = new[] { ConfigurationManager.AppSettings["ValidAudience"] },
                    ValidIssuers = new[] { ConfigurationManager.AppSettings["ValidIssuer"] },
                    TokenHandler = config.GetAppServiceTokenHandler()
                });
            }
            app.UseWebApi(config);
            ConfigureSwagger(config);
        }
    }

    public class impulsevidInitializer : DropCreateDatabaseIfModelChanges<impulsevidContext>
    {
        protected override void Seed(impulsevidContext context)
        {
            List<TodoItem> todoItems = new List<TodoItem>
            {
                new TodoItem { Id = Guid.NewGuid().ToString(), Text = "Second item", Complete = false }
            };

            foreach (TodoItem todoItem in todoItems)
            {
                context.Set<TodoItem>().Add(todoItem);
            }
            context.SaveChanges();
            List<Profile> profiles = new List<Profile>
            {
                new Profile { Id = "999999", Username = "Ghettoman"}
        
            };
            foreach (Profile p in profiles)
            {
                context.Set<Profile>().Add(p);
            }
            context.SaveChanges();
            List<Video> videos = new List<Video>
            {
                new Video { Id = Guid.NewGuid().ToString(), Title = "First Video",ProfileID = "999999"}

            };
            foreach (Video v in videos)
            {
                context.Set<Video>().Add(v);
            }
            context.SaveChanges();
            List<Comment> comments = new List<Comment>
            {
                new Comment { Id = Guid.NewGuid().ToString(), VideoID = "First Video",ProfileID = "999999",CommentContent = "Hey You Guys"}

            };
            foreach (Comment c in comments)
            {
                context.Set<Comment>().Add(c);
            }
            context.SaveChanges();
            base.Seed(context);
        }
    }
}

