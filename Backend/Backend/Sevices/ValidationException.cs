﻿using System.Runtime.Serialization;

namespace Backend.Sevices
{
  [Serializable]
  internal class ValidationException : Exception
  {
    public ValidationException()
    {
    }

    public ValidationException(string? message) : base(message)
    {
    }

    public ValidationException(string? message, Exception? innerException) : base(message, innerException)
    {
    }

    protected ValidationException(SerializationInfo info, StreamingContext context) : base(info, context)
    {
    }
  }
}