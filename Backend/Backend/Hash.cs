using System.Security.Cryptography;
using System.Text;

namespace Backend
{
  public class Hash
  {
    private HashAlgorithm _algoritmo;

    public Hash(HashAlgorithm Algorithm)
    {
      _algoritmo = Algorithm;
    }

    public string Cryptograph(string senha)
    {
      byte[] encodedValue = Encoding.UTF8.GetBytes(senha);
      byte[] encryptedPassword = _algoritmo.ComputeHash(encodedValue);

      StringBuilder sb = new StringBuilder();
      foreach (var caracter in encryptedPassword)
      {
        sb.Append(caracter.ToString("X2"));
      }

      return sb.ToString();
    }

    public bool Verify(string typedPassword, string registeredHash)
    {
      byte[] encryptedPassword = _algoritmo.ComputeHash(Encoding.UTF8.GetBytes(typedPassword));

      StringBuilder sb = new StringBuilder();
      foreach (var caractere in encryptedPassword)
      {
        sb.Append(caractere.ToString("X2"));
      }

      return sb.ToString() == registeredHash;
    }
  }
}
