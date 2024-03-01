using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;

namespace Backend.Models.Database;

public partial class MessageDbContext : DbContext
{
  public DbSet<LoggedUser> LoggedUser { get; set; }
}
