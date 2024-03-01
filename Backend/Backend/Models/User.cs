namespace Backend.Models
{
  public class User
  {
    public User(string connectionId, int id, string name)
    {
      Id = id;
      Name = name;
      ConnectionId = connectionId;
    }

    public string ConnectionId { get; set; }
    public int Id { get; set; }
    public string Name { get; set; }
    public string? ChatingTo { get; set; }
  }
}
