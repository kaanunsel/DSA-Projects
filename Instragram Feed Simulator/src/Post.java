/**
 * Represents a social media post with attributes like ID, content, author, and like count.
 * Implements Comparable to allow sorting based on like count and post ID.
 */
public class Post implements Comparable<Post> {
    private String postId; // Unique identifier for the post
    private String content; // Content of the post
    private int likeCount; // Number of likes on the post
    private User author; // Author of the post
    private MyHashTable<String, User> likedBy; // Tracks users who liked the post

    /**
     * Constructor to initialize a new Post.
     * @param postId Unique ID of the post.
     * @param content Content of the post.
     * @param author Author of the post.
     */
    public Post(String postId, String content, User author) {
        this.postId = postId;
        this.content = content;
        this.likeCount = 0; // Initialize with zero likes
        this.author = author;
        this.likedBy = new MyHashTable<>(); // Tracks users who liked the post
    }

    // Getter for Post ID
    public String getPostId() {
        return postId;
    }

    // Getter for Content
    public String getContent() {
        return content;
    }

    // Getter for Like Count
    public int getLikeCount() {
        return likeCount;
    }

    // Getter for Author
    public User getAuthor() {
        return author;
    }

    /**
     * Toggles the like/unlike state for a user on this post.
     * If the user has already liked the post, it will unlike it. Otherwise, it will like the post.
     * @param user The user performing the like/unlike action.
     * @return 1 if the post is liked, -1 if unliked.
     */
    public int toggleLike(User user) {
        user.markPostAsSeen(this); // Mark the post as seen by the user
        if (likedBy.containsKey(user.getUserId())) {
            // Unlike the post if the user already liked it
            likedBy.remove(user.getUserId());
            likeCount--;
            return -1; // Indicates the post was unliked
        } else {
            // Like the post if the user hasn't liked it yet
            likedBy.put(user.getUserId(), user);
            likeCount++;
            return 1; // Indicates the post was liked
        }
    }

    /**
     * Checks whether a specific user has liked this post.
     * @param user The user to check.
     * @return True if the user has liked the post, false otherwise.
     */
    public boolean isLikedBy(User user) {
        return likedBy.containsKey(user.getUserId());
    }

    /**
     * Compares this post to another post for sorting purposes.
     * Posts are sorted by like count in descending order.
     * If two posts have the same like count, they are sorted by their post ID in lexicographical order.
     * @param other The other post to compare to.
     * @return A negative value if this post is "greater," positive if "lesser," 0 if equal.
     */
    @Override
    public int compareTo(Post other) {
        if (this.likeCount != other.likeCount) {
            return Integer.compare(this.likeCount, other.likeCount);
        }
        return this.postId.compareTo(other.postId);
    }

    /**
     * Returns a string representation of the post for logging and debugging purposes.
     * @return A string containing the post ID, author ID, and like count.
     */
    @Override
    public String toString() {
        return "Post ID: " + postId + ", Author: " + author.getUserId() + ", Likes: " + likeCount;
    }
}