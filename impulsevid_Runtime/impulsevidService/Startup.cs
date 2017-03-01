using Microsoft.Owin;
using Owin;

[assembly: OwinStartup(typeof(impulsevidService.Startup))]

namespace impulsevidService
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureMobileApp(app);
        }
    }
}