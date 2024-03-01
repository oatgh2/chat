using Backend.Models;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace Backend
{
  public class CustomController : ControllerBase
  {
    public LoggedUser? loggedUser
    {
      get
      {
        if (HttpContext.User != null)
        {
          try
          {
            LoggedUser user = new LoggedUser();
            user.UserName = HttpContext.User.Claims.FirstOrDefault(x => x.Type!.Equals("Name"))!.Value;
            user.Id = Convert.ToInt32(HttpContext.User.Claims.FirstOrDefault(x => x.Type!.Equals("Id"))!.Value);
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
