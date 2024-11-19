import java.io.*;
import java.util.ArrayList;

/**
 * Entry point for the Feed Management system.
 * Processes commands from an input file, executes them via the FeedManagement class,
 * and writes the results to an output file.
 */
public class Main {
    public static void main(String[] args) {
        // Input and output file names are provided as command-line arguments
        String inputFile = args[0];
        String outputFile = args[1];

        // Centralized manager to handle user, post, and feed operations
        FeedManagement feedManagement = new FeedManagement();

        // Try-with-resources to ensure proper closure of file streams
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {

            String line;
            // Process each line in the input file
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" "); // Split the line into command and parameters
                String command = parts[0]; // Extract the command

                try {
                    // Handle commands based on their type
                    switch (command) {
                        case "create_user":
                            handleCreateUser(parts, feedManagement, writer);
                            break;
                        case "create_post":
                            handleCreatePost(parts, feedManagement, writer);
                            break;
                        case "follow_user":
                            handleFollowUser(parts, feedManagement, writer);
                            break;
                        case "unfollow_user":
                            handleUnfollowUser(parts, feedManagement, writer);
                            break;
                        case "generate_feed":
                            handleGenerateFeed(parts, feedManagement, writer);
                            break;
                        case "sort_posts":
                            handleSortPosts(parts, feedManagement, writer);
                            break;
                        case "see_post":
                            handleSeePost(parts, feedManagement, writer);
                            break;
                        case "see_all_posts_from_user":
                            handleSeeAllPostsFromUser(parts, feedManagement, writer);
                            break;
                        case "toggle_like":
                            handleToggleLike(parts, feedManagement, writer);
                            break;
                        case "scroll_through_feed":
                            handleScrollThroughFeed(parts, feedManagement, writer);
                            break;
                        default:
                            // Log an unknown command
                            writer.println("Unknown command: " + command);
                    }
                } catch (Exception e) {
                    // Log any errors that occur while executing a command
                    writer.println("Error executing command: " + command);
                }
            }
        } catch (FileNotFoundException e) {
            // Handle missing input file
            System.out.println("Input file not found: " + inputFile);
        } catch (IOException e) {
            // Handle I/O errors
            System.out.println("Error reading or writing files.");
        }
    }

    // Handles the "create_user" command
    private static void handleCreateUser(String[] parts, FeedManagement feedManagement, PrintWriter writer) {
        String userId = parts[1];
        boolean success = feedManagement.createUser(userId);

        if (success) {
            writer.println("Created user with Id " + userId + ".");
        } else {
            writer.println("Some error occurred in create_user.");
        }
    }

    // Handles the "create_post" command
    private static void handleCreatePost(String[] parts, FeedManagement feedManagement, PrintWriter writer) {
        String userId = parts[1];
        String postId = parts[2];
        String content = parts[3];
        boolean success = feedManagement.createPost(userId, postId, content);

        if (success) {
            writer.println(userId + " created a post with Id " + postId + ".");
        } else {
            writer.println("Some error occurred in create_post.");
        }
    }

    // Handles the "follow_user" command
    private static void handleFollowUser(String[] parts, FeedManagement feedManagement, PrintWriter writer) {
        String followerId = parts[1];
        String followeeId = parts[2];

        // Prevent a user from following themselves
        if (followerId.equals(followeeId)) {
            writer.println("Some error occurred in follow_user.");
            return;
        }

        boolean success = feedManagement.followUser(followerId, followeeId);

        if (success) {
            writer.println(followerId + " followed " + followeeId + ".");
        } else {
            writer.println("Some error occurred in follow_user.");
        }
    }

    // Handles the "unfollow_user" command
    private static void handleUnfollowUser(String[] parts, FeedManagement feedManagement, PrintWriter writer) {
        String followerId = parts[1];
        String followeeId = parts[2];

        // Prevent a user from unfollowing themselves
        if (followerId.equals(followeeId)) {
            writer.println("Some error occurred in unfollow_user.");
            return;
        }

        boolean success = feedManagement.unfollowUser(followerId, followeeId);

        if (success) {
            writer.println(followerId + " unfollowed " + followeeId + ".");
        } else {
            writer.println("Some error occurred in unfollow_user.");
        }
    }

    // Handles the "generate_feed" command
    private static void handleGenerateFeed(String[] parts, FeedManagement feedManagement, PrintWriter writer) {
        String userId = parts[1];
        int num = Integer.parseInt(parts[2]); // Number of posts to generate in the feed

        ArrayList<Post> feed = feedManagement.generateFeed(userId, num);

        if (feed == null) {
            writer.println("Some error occurred in generate_feed.");
        } else {
            writer.println("Feed for " + userId + ":");
            for (Post post : feed) {
                writer.println(post.toString());
            }
            if (feed.size() < num) {
                writer.println("No more posts available for " + userId + ".");
            }
        }
    }

    // Handles the "sort_posts" command
    private static void handleSortPosts(String[] parts, FeedManagement feedManagement, PrintWriter writer) {
        String userId = parts[1];
        ArrayList<String> sortedPosts = feedManagement.sortPosts(userId);

        if (sortedPosts == null) {
            writer.println("Some error occurred in sort_posts.");
        } else if (sortedPosts.isEmpty()) {
            writer.println("No posts from " + userId + ".");
        } else {
            writer.println("Sorting " + userId + "'s posts:");
            for (String post : sortedPosts) {
                writer.println(post);
            }
        }
    }

    // Handles the "see_post" command
    private static void handleSeePost(String[] parts, FeedManagement feedManagement, PrintWriter writer) {
        String userId = parts[1];
        String postId = parts[2];

        boolean success = feedManagement.seePost(userId, postId);

        if (success) {
            writer.println(userId + " saw " + postId + ".");
        } else {
            writer.println("Some error occurred in see_post.");
        }
    }

    // Handles the "see_all_posts_from_user" command
    private static void handleSeeAllPostsFromUser(String[] parts, FeedManagement feedManagement, PrintWriter writer) {
        String viewerId = parts[1];
        String viewedId = parts[2];
        boolean success = feedManagement.seeAllPostsFromUser(viewerId, viewedId);

        if (success) {
            writer.println(viewerId + " saw all posts of " + viewedId + ".");
        } else {
            writer.println("Some error occurred in see_all_posts_from_user.");
        }
    }

    // Handles the "toggle_like" command
    private static void handleToggleLike(String[] parts, FeedManagement feedManagement, PrintWriter writer) {
        String userId = parts[1];
        String postId = parts[2];
        int success = feedManagement.toggleLike(userId, postId);

        if (success == 1) {
            writer.println(userId + " liked " + postId + ".");
        } else if (success == -1) {
            writer.println(userId + " unliked " + postId + ".");
        } else {
            writer.println("Some error occurred in toggle_like.");
        }
    }

    // Handles the "scroll_through_feed" command
    private static void handleScrollThroughFeed(String[] parts, FeedManagement feedManagement, PrintWriter writer) {
        String userId = parts[1];
        int num = Integer.parseInt(parts[2]); // Number of posts to scroll through

        // Parse the like/dislike actions for the posts
        ArrayList<Boolean> likes = new ArrayList<>();
        for (int i = 3; i < parts.length; i++) {
            likes.add(parts[i].equals("1")); // Add true for '1' and false for '0'
        }

        // Get the scroll log from FeedManagement
        ArrayList<String> scrollLog = feedManagement.scrollThroughFeed(userId, num, likes);

        if (scrollLog == null) {
            writer.println("Some error occurred in scroll_through_feed.");
            return;
        }

        // Log scrolling activity
        writer.println(userId + " is scrolling through feed:");
        for (String log : scrollLog) {
            writer.println(log);
        }
    }
}