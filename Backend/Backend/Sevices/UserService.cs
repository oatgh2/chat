using Backend.Models;
using Backend.Models.Database;
using Microsoft.Data.SqlClient;
using Microsoft.EntityFrameworkCore;
using Npgsql;

namespace Backend.Sevices
{
  public class UserService : Service
  {
    public UserService(MessageDbContext context, IHttpContextAccessor httpContextAccessor) : base(context, httpContextAccessor)
    {

    }

    public int CreateUser(CreateUser registerModel)
    {
      Usuario user = new Usuario();
      user.Nomeusuario = registerModel.Username!;
      user.Senha = registerModel.Password!.GenHash();
      Context.Entry(user).State = EntityState.Added;
      Context.SaveChanges();
      return user.Id;
    }

    public void DeslogUser()
    {
      Usuario? user = Context.Usuarios.FirstOrDefault(x => x.Nomeusuario.Equals(loggedUser!.UserName));
      if(user != null)
      {
        user.Logado = false;
        Context.Entry(user).State = EntityState.Modified;
        Context.SaveChanges();
      }
    }

    public LoggedUser? GetLoggedUser(Login login)
    {
      Usuario? user = Context.Usuarios.FirstOrDefault(x => x.Nomeusuario.Equals(login.Username));

      if (user != null)
      {
        if (user.Logado == true)
          throw new ValidationException("Usuário já está logado");

        bool logged = login.Password!.VerifyPassword(user.Senha);
        if (logged)
        {
          LoggedUser loggedUser = new LoggedUser()
          {
            Id = user!.Id,
            UserName = user.Nomeusuario,

          };
          user.Logado = true;
          Context.Entry(user).State = EntityState.Modified;
          Context.SaveChanges();
          return loggedUser;
        }else
          return null;
      }
      return null;
    }
  }
}
