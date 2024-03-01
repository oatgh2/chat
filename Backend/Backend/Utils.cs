using Backend.Models;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;

namespace Backend
{

  public static class Utils
  {
    public static string JwtKey = "{924805D7-BFFF-4401-8547-41A8928B696D}";

    public static string ToJwtToken(this LoggedUser loggedUser)
    {
      JwtSecurityTokenHandler tokenHandler = new JwtSecurityTokenHandler();

      SecurityTokenDescriptor tokenDescriptor = new SecurityTokenDescriptor()
      {
        Subject = new ClaimsIdentity(new Claim[] {
        new Claim("Id", loggedUser.Id!.ToString()!),
        new Claim("Name", loggedUser.UserName!),
        }),
        SigningCredentials = new SigningCredentials(new SymmetricSecurityKey(Encoding.UTF8.GetBytes(JwtKey)), SecurityAlgorithms.HmacSha256Signature)
      };
      SecurityToken token = tokenHandler.CreateToken(tokenDescriptor);
      return tokenHandler.WriteToken(token);
    }

    public static string GenHash(this string password)
    {
      Hash hash = new Hash(SHA512.Create());
      string result = hash.Cryptograph(password);
      return result;
    }

    public static bool VerifyPassword(this string password, string savedHash)
    {
      Hash hash = new Hash(SHA512.Create());
      bool result = hash.Verify(password, savedHash);
      return result;
    }


  }
}
