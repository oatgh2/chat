using System;
using System.Collections.Generic;

namespace Backend.Models.Database;

public partial class Usuario
{
  public int Id { get; set; }

  public string Nomeusuario { get; set; } = null!;

  public string Senha { get; set; } = null!;

  public bool? Logado { get; set; } = false;
}
