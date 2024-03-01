using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.IO;
namespace Backend.Controllers
{
  [Authorize]
  [Route("File")]
  public class FileController : ControllerBase
  {
    [HttpGet]
    [Route("Audio/{audioId}")]
    public IActionResult Audio([FromQuery]string audioId)
    {
      string id = Guid.NewGuid().ToString();
      string path = Path.Combine(Environment.CurrentDirectory, @"audios\");
      Directory.CreateDirectory(path);
      path += string.Format("{0}.amr", id);
      byte[] readedBytes = System.IO.File.ReadAllBytes(path);
      return File(readedBytes, "audio/amr");
    }
  }
}
