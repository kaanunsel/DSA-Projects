/**
 * Represents a user in the system with attributes for their ID, followed users, posts,
 * and a record of seen posts.
 */
public class User {
    private String userId; // Unique identifier for the user
    private MyLinkedList<User> followedUsers; // List of users this user is following
    private MyLinkedList<Post> posts; // List of posts created by this user
    private MyHashTable<String, Post> seenPosts; // Tracks posts seen by this user

    /**
     * Constructor to initialize a new User.
     * @param userId Unique ID for the user.
     */
    public User(String userId) {
        this.userId = userId;
        this.followedUsers = new MyLinkedList<>(); // Initialize the list of followed users
        this.posts = new MyLinkedList<>(); // Initialize the list of user's posts
        this.seenPosts = new MyHashTable<>(); // Initialize the hash table of seen posts
    }

    // Getter for User ID
    public String getUserId() {
        return userId;
    }

    /**
     * Follows another user.
     * @param user The user to follow.
     * @return True if the user is successfully followed; false if already following.
     */
    public boolean follow(User user) {
        if (followedUsers.contains(user)) {
            return false; // Already following this user
        }
        followedUsers.add(user);
        return true;
    }

    /**
     * Unfollows a user.
     * @param user The user to unfollow.
     * @return True if the user is successfully unfollowed; false if not currently following.
     */
    public boolean unfollow(User user) {
        if (!followedUsers.contains(user)) {
            return false; // Not currently following this user
        }
        followedUsers.removeByValue(user); // Remove the user from the followed list
        return true;
    }

    /**
     * Adds a new post created by this user.
     * @param post The post to add.
     */
    public void addPost(Post post) {
        posts.add(post);
    }

    /**
     * Retrieves all posts created by this user.
     * @return A list of posts created by this user.
     */
    public MyLinkedList<Post> getPosts() {
        return posts;
    }

    /**
     * Retrieves the list of users this user is following.
     * @return A list of followed users.
     */
    public MyLinkedList<User> getFollowedUsers() {
        return followedUsers;
    }

    /**
     * Marks a post as seen by this user.
     * @param post The post to mark as seen.
     * @return True after marking the post as seen.
     */
    public boolean markPostAsSeen(Post post) {
        seenPosts.put(post.getPostId(), post); // Add the post to the seen posts hash table
        return true;
    }

    /**
     * Checks whether this user has already seen a given post.
     * @param post The post to check.
     * @return True if the post has been seen; false otherwise.
     */
    public boolean hasSeenPost(Post post) {
        return seenPosts.containsKey(post.getPostId());
    }
}