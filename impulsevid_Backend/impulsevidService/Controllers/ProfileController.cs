using System.Linq;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Controllers;
using System.Web.Http.OData;
using Microsoft.Azure.Mobile.Server;
using impulsevidService.DataObjects;
using impulsevidService.Models;

namespace impulsevidService.Controllers
{
    public class ProfileController : TableController<Profile>
    {
        protected override void Initialize(HttpControllerContext controllerContext)
        {
            base.Initialize(controllerContext);
            impulsevidContext context = new impulsevidContext();
            DomainManager = new EntityDomainManager<Profile>(context, Request);
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
            return CreatedAtRoute("Tables", new { id = current.Uid }, current);
        }

        // DELETE tables/Profile/48D68C86-6EA6-4C25-AA33-223FC9A27959
        public Task DeleteProfile(string id)
        {
             return DeleteAsync(id);
        }
    }
}
