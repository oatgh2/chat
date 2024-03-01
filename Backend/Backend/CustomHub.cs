using Backend.Models;
using Microsoft.AspNetCore.SignalR;

namespace Backend
{
  public class CustomHub : Hub
  {
    public LoggedUser? loggedUser
    {
      get
      {
        if (Context.User != null)
        {
          try
          {
            LoggedUser user = new LoggedUser();
            user.UserName = Context.User.Claims.FirstOrDefault(x => x.Type!.Equals("Name"))!.Value;
            user.Id = Convert.ToInt32(Context.User.Claims.FirstOrDefault(x => x.Type!.Equals("Id"))!.Value);
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
  }
}
