using Backend.Models;
using Backend.Models.Database;

namespace Backend.Sevices
{
  public abstract class Service
  {
    protected MessageDbContext Context { get; set; }
    HttpContext httpContext { get; set; }
    protected LoggedUser? loggedUser
    {
      get
      {
        if (httpContext.User != null)
        {
          try
          {
            LoggedUser user = new LoggedUser();
            user.UserName = httpContext.User.Claims.FirstOrDefault(x => x.Type!.Equals("Name"))!.Value;
            user.Id = Convert.ToInt32(httpContext.User.Claims.FirstOrDefault(x => x.Type!.Equals("Id"))!.Value);
            return user;
          }
          catch (Exception ex)
          {
            return null;
          }
        }
        else
          return null;
      }
    }
    public Service(MessageDbContext context, IHttpContextAccessor httpContextAccessor)
    {
      Context = context;
      httpContext = httpContextAccessor.HttpContext!;
    }
  }
}
