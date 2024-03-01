namespace Backend.Models
{
  public class LoggedUser
  {
    public int Id { get; set; }
    public string UserName { get; set; }
    public string? JWT { get; set; }

  }
}
