namespace Backend.Models
{
  public class Message
  {
    public Message()
    {
    }

    public Message(Guid id, string user, string text)
    {
      Id = id;
      User = user;
      Text = text;
    }

    public Guid Id { get; set; }
    public string User { get; set; }
    public string Text { get; set; }
    public bool IsAudio { get; set; }
  }
}
