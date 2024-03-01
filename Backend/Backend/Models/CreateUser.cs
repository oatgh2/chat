using System.ComponentModel.DataAnnotations;

namespace Backend.Models
{
  public class CreateUser
  {
    [Required]
    public string? Username { get; set; }
    [Required]
    public string? Password { get; set; }
  }
}
