import java.util.ArrayList;

/**
 * FeedManagement handles core operations of the Instagram-like system,
 * such as user and post management, following/unfollowing users, and feed generation.
 */
public class FeedManagement {
    private MyHashTable<String, User> users; // Stores users by their IDs
    private MyHashTable<String, Post> posts; // Stores posts by their IDs

    // Constructor: Initializes user and post hash tables
    public FeedManagement() {
        users = new MyHashTable<>();
        posts = new MyHashTable<>();
    }

    /**
     * Creates a new user with the given ID.
     * @param userId Unique ID for the user.
     * @return True if the user is successfully created; false if the user already exists.
     */
    public boolean createUser(String userId) {
        if (users.containsKey(userId)) {
            return false; // User already exists
        }
        users.put(userId, new User(userId));
        return true;
    }

    /**
     * Creates a new post for a user.
     * @param userId ID of the user creating the post.
     * @param postId Unique ID for the post.
     * @param content Content of the post.
     * @return True if the post is successfully created; false if user doesn't exist or post ID is taken.
     */
    public boolean createPost(String userId, String postId, String content) {
        if (!users.containsKey(userId)) {
            return false; // User doesn't exist
        }
        if (posts.containsKey(postId)) {
            return false; // Post ID already exists
        }
        User user = users.get(userId);
        Post post = new Post(postId, content, user);
        posts.put(postId, post);
        user.addPost(post);
        return true;
    }

    /**
     * Allows a user to follow another user.
     * @param followerId ID of the follower.
     * @param followeeId ID of the user to be followed.
     * @return True if the follow operation is successful; false otherwise.
     */
    public boolean followUser(String followerId, String followeeId) {
        if (!users.containsKey(followerId) || !users.containsKey(followeeId)) {
            return false; // Either user doesn't exist
        }
        User follower = users.get(followerId);
        User followee = users.get(followeeId);
        return follower.follow(followee);
    }

    /**
     * Allows a user to unfollow another user.
     * @param followerId ID of the follower.
     * @param followeeId ID of the user to be unfollowed.
     * @return True if the unfollow operation is successful; false otherwise.
     */
    public boolean unfollowUser(String followerId, String followeeId) {
        if (!users.containsKey(followerId) || !users.containsKey(followeeId)) {
            return false; // Either user doesn't exist
        }
        User follower = users.get(followerId);
        User followee = users.get(followeeId);
        return follower.unfollow(followee);
    }

    /**
     * Marks a post as seen by a user.
     * @param userId ID of the user.
     * @param postId ID of the post.
     * @return True if the operation is successful; false otherwise.
     */
    public boolean seePost(String userId, String postId) {
        if (!users.containsKey(userId) || !posts.containsKey(postId)) {
            return false; // User or post doesn't exist
        }
        User user = users.get(userId);
        Post post = posts.get(postId);
        user.markPostAsSeen(post);
        return true;
    }

    /**
     * Marks all posts from a specific user as seen by another user.
     * @param viewerId ID of the user viewing posts.
     * @param viewedId ID of the user whose posts are being viewed.
     * @return True if the operation is successful; false otherwise.
     */
    public boolean seeAllPostsFromUser(String viewerId, String viewedId) {
        if (!users.containsKey(viewerId) || !users.containsKey(viewedId)) {
            return false; // Either user doesn't exist
        }
        User viewer = users.get(viewerId);
        User viewed = users.get(viewedId);
        for (Post post : viewed.getPosts()) {
            viewer.markPostAsSeen(post);
        }
        return true;
    }

    /**
     * Toggles the like status of a post by a user.
     * @param userId ID of the user.
     * @param postId ID of the post.
     * @return 1 if the post is liked, -1 if unliked, 0 if an error occurs.
     */
    public int toggleLike(String userId, String postId) {
        if (!users.containsKey(userId) || !posts.containsKey(postId)) {
            return 0; // User or post doesn't exist
        }
        Post post = posts.get(postId);
        return post.toggleLike(users.get(userId));
    }

    /**
     * Generates a feed for a user based on the posts of followed users.
     * @param userId ID of the user requesting the feed.
     * @param num Number of posts to include in the feed.
     * @return A list of posts for the user's feed, or null if the user doesn't exist.
     */
    public ArrayList<Post> generateFeed(String userId, int num) {
        if (!users.containsKey(userId)) {
            return null; // User doesn't exist
        }
        User user = users.get(userId);
        MaxHeap<Post> feedHeap = new MaxHeap<>();

        // Add unseen posts from followed users to the heap
        for (User followee : user.getFollowedUsers()) {
            for (Post post : followee.getPosts()) {
                if (!user.hasSeenPost(post)) {
                    feedHeap.add(post);
                }
            }
        }

        // Extract top posts from the heap to form the feed
        ArrayList<Post> feed = new ArrayList<>();
        for (int i = 0; i < num && feedHeap.size() > 0; i++) {
            feed.add(feedHeap.extractMax());
        }
        return feed;
    }

    /**
     * Simulates scrolling through a user's feed and interacting with posts.
     * @param userId ID of the user.
     * @param num Number of posts to scroll through.
     * @param likes List of boolean values indicating like/dislike actions.
     * @return A log of the scrolling actions, or null if the user doesn't exist.
     */
    public ArrayList<String> scrollThroughFeed(String userId, int num, ArrayList<Boolean> likes) {
        if (!users.containsKey(userId)) {
            return null; // User doesn't exist
        }

        User user = users.get(userId);
        ArrayList<Post> feed = generateFeed(userId, num);

        ArrayList<String> scrollLog = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            if (i >= feed.size()) { // Not enough posts available
                scrollLog.add("No more posts in feed.");
                break;
            }

            Post post = feed.get(i);
            user.markPostAsSeen(post); // Mark post as seen

            if (likes.get(i)) {
                post.toggleLike(user); // Like the post
                scrollLog.add(userId + " saw " + post.getPostId() + " while scrolling and clicked the like button.");
            } else {
                scrollLog.add(userId + " saw " + post.getPostId() + " while scrolling.");
            }
        }

        return scrollLog;
    }

    /**
     * Sorts a user's posts by their like count in descending order.
     * @param userId ID of the user.
     * @return A list of sorted post details, or null if the user doesn't exist.
     */
    public ArrayList<String> sortPosts(String userId) {
        if (!users.containsKey(userId)) {
            return null; // User doesn't exist
        }
        User user = users.get(userId);
        ArrayList<String> sortedPosts = new ArrayList<>();
        MaxHeap<Post> sortHeap = new MaxHeap<>();

        // Add posts to a max heap
        for (Post post : user.getPosts()) {
            sortHeap.add(post);
        }

        // Extract posts from the heap in sorted order
        while (sortHeap.size() != 0) {
            Post post = sortHeap.extractMax();
            sortedPosts.add(post.getPostId() + ", Likes: " + post.getLikeCount());
        }
        return sortedPosts;
    }
}