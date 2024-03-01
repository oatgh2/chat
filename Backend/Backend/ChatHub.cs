using Backend;
using Backend.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.SignalR;
using static System.Net.Mime.MediaTypeNames;

[Authorize]
public class ChatHub : CustomHub
{
  List<User> users;
  List<Message> messages;
  public ChatHub(HubManager manager)
  {
    users = manager.users;
    messages = manager.messages;
  }

  public async Task GetMessages()
  {
    await Clients.Client(Context.ConnectionId).SendAsync("get_all_messages", messages);
  }

  public void SendAudio(string audioBase64)
  {
    string id = Guid.NewGuid().ToString();
    string path = Path.Combine(Environment.CurrentDirectory,@"audios\");
    Directory.CreateDirectory(path);
    path += string.Format("{0}.amr", id);
    byte[] bytes = Convert.FromBase64String(audioBase64);
    File.WriteAllBytes(path, bytes);
    Message toAddMessage = new Message()
    {
      Id = Guid.NewGuid(),
      Text = id,
      User = loggedUser!.UserName,
      IsAudio = true,
    };
    messages.Add(toAddMessage);
    Parallel.ForEach(users, (user) =>
    {
      Clients.Client(user.ConnectionId).SendAsync("get_message", toAddMessage);
    });
  }

  public void SendMessage(string message)
  {
    Message toAddMessage = new Message()
    {
      Id = Guid.NewGuid(),
      Text = message,
      User = loggedUser!.UserName
    };
    messages.Add(toAddMessage);
    Parallel.ForEach(users, (user) =>
    {
      Clients.Client(user.ConnectionId).SendAsync("get_message", toAddMessage);
    });
  }
  
  public override Task OnConnectedAsync()
  {
    User user = new User(Context.ConnectionId, loggedUser!.Id, loggedUser.UserName);;
    users.Add(user);
    return base.OnConnectedAsync();
  }

  public override Task OnDisconnectedAsync(Exception? exception)
  {
    users.RemoveAll(x => x.Id == loggedUser!.Id);
    return base.OnDisconnectedAsync(exception);
  }
}