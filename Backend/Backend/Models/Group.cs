namespace Backend.Models
{
    public class Group
    {
        public Group(Guid id, string name)
        {
            Id = id;
            Name = name;
        }

        public Guid Id { get; set; }
        public string Name { get; set; }
        public List<Message> Messages { get; private set; } = new List<Message>();
        public List<User> Users { get; private set; } = new List<User>();
    }
}
