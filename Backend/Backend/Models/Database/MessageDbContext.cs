using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;

namespace Backend.Models.Database;

public partial class MessageDbContext : DbContext
{
  public MessageDbContext()
  {
  }

  public MessageDbContext(DbContextOptions<MessageDbContext> options)
      : base(options)
  {
  }

  public virtual DbSet<Usuario> Usuarios { get; set; }



  protected override void OnModelCreating(ModelBuilder modelBuilder)
  {
    modelBuilder.Entity<Usuario>(entity =>
    {
      entity.HasKey(e => e.Id).HasName("usuarios_pk");

      entity.ToTable("usuarios");

      entity.HasIndex(e => e.Nomeusuario, "usuarios_nomeusuario_idx");

      entity.HasIndex(e => e.Nomeusuario, "usuarios_unique").IsUnique();

      entity.Property(e => e.Id)
              .UseIdentityAlwaysColumn()
              .HasColumnName("id");
      entity.Property(e => e.Nomeusuario)
              .HasMaxLength(256)
              .HasColumnName("nomeusuario");
      entity.Property(e => e.Senha)
              .HasMaxLength(256)
              .HasColumnName("senha");

      entity.Property(property => property.Logado).HasColumnName("logado");
    });

    OnModelCreatingPartial(modelBuilder);
  }

  partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
