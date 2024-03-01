using Backend.Models;
using Backend.Models.Database;
using Backend.Sevices;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace Backend.Controllers
{
  [Route("User")]
  [Authorize]
  public class UserController : CustomController
  {
    private UserService _userService;
    List<User> users;
    List<Message> messages;
    public UserController(UserService service, HubManager manager)
    {
      _userService = service;
      users = manager.users;
      messages = manager.messages;
    }

    [HttpPost]
    [Route("Register")]
    [AllowAnonymous]
    public ActionResult Register([FromBody] CreateUser user)
    {
      try
      {
        if (ModelState.IsValid)
        {
          int id = _userService.CreateUser(user);
          return Ok(new
          {
            error = false,
            message = "Sucesso ao criar",
            data = new
            {
              id
            }
          });
        }
        else
        {
          return BadRequest(new
          {
            error = true,
            message = "Erros de validação",
            validations = ModelState.ValidationState
          });
        }
      }
      catch (Exception ex)
      {
        return BadRequest(new
        {
          error = true,
          message = "Ocorreu um erro, tente novamente mais tarde",
        });
      }
    }

    [HttpGet("Ping")]
    public ActionResult Ping()
    {
      return Ok(new { message="pong", loggedUser });
    }

    [HttpGet("DeslogUser")]
    public ActionResult DeslogUser()
    {
      try
      {
        _userService.DeslogUser();
        return Ok(new
        {
          error = false,
          message = "Deslogado",
        });
      }
      catch (Exception ex)
      {
        return BadRequest(new
        {
          error = true,
          message = "An error occurred, try again later",
        });
      }
    }

    [HttpPost]
    [Route("Login")]
    [AllowAnonymous]
    public ActionResult Login([FromBody] Login login)
    {
      try
      {
        if (!ModelState.IsValid)
        {
          return BadRequest(new
          {
            error = true,
            message = "Login incorreto",
          });
        }

        int countOfLoggedsWithSameName = users.Count(u => u.Name == login.Username);

        if (countOfLoggedsWithSameName > 0)
        {
          return Ok(new
          {
            error = true,
            message = "Já está logado",
          });
        }

        LoggedUser? loggedUser = _userService.GetLoggedUser(login);

        if (loggedUser != null)
        {
          loggedUser.JWT = loggedUser.ToJwtToken();
          return Ok(new
          {
            error = false,
            message = "Logado com sucesso",
            data = loggedUser
          });
        }
        else
        {
          return Ok(new
          {
            error = true,
            message = "Login incorreto",
          });
        }
      }
      catch (ValidationException ex)
      {
        return BadRequest(new
        {
          error = true,
          message = ex.Message,
        });
      }
      catch (Exception ex)
      {
        return BadRequest(new
        {
          error = true,
          message = "Ocorreu um erro, tente novamente mais tarde",
        });
      }
    }
  }
}
