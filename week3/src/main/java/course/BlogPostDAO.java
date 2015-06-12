package course;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.QueryBuilder;

import java.util.Date;
import java.util.List;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import static com.mongodb.client.model.Filters.eq;


public class BlogPostDAO {
    MongoCollection<Document> postsCollection;

    public BlogPostDAO(final MongoDatabase blogDatabase) {
        postsCollection = blogDatabase.getCollection("posts");
    }

    // Return a single post corresponding to a permalink
    public Document findByPermalink(String permalink) {

        // XXX HW 3.2,  Work Here
        Document post = null;

       
        post=postsCollection.find(eq("permalink", permalink)).first();
        
        /*QueryBuilder query = QueryBuilder.start("permalink").is(permalink);
		post = postsCollection.find(query);*/

        return post;
    }

    // Return a list of posts in descending order. Limit determines
    // how many posts are returned.
    public List<Document> findByDateDescending(int limit) {

        // XXX HW 3.2,  Work Here
        // Return a list of DBObjects, each one a post from the posts collection
        List<Document> posts = new ArrayList<Document>();
        
        FindIterable<Document> itr = postsCollection.find(new BasicDBObject())
				.sort(new BasicDBObject("date", -1)).limit(limit);
		for (Document dbObject : itr) {
			if (dbObject != null) {
				posts.add(dbObject);
			}
		}

        return posts;
    }


    public String addPost(String title, String body, List tags, String username) {

        System.out.println("inserting blog entry " + title + " " + body);

        String permalink = title.replaceAll("\\s", "_"); // whitespace becomes _
        permalink = permalink.replaceAll("\\W", ""); // get rid of non alphanumeric
        permalink = permalink.toLowerCase();


        // XXX HW 3.2, Work Here
        // Remember that a valid post has the following keys:
        // author, body, permalink, tags, comments, date
        //
        // A few hints:
        // - Don't forget to create an empty list of comments
        // - for the value of the date key, today's datetime is fine.
        // - tags are already in list form that implements suitable interface.
        // - we created the permalink for you above.

        /*BasicDBList btags = new BasicDBList();
        btags.add(tags.get(1));
        
        BasicDBList comments = new BasicDBList();
        
        // Build the post object and insert it
        Document post = new Document("title",title)
        		.append("author",username)
        		.append("body",body)
        		.append("permalink",permalink)
        		.append("tags",btags)
        		.append("comments",comments)
        		.append("date",new Date());
        
        postsCollection.insertOne(post);*/


        Document post = new Document();
        
        post.append("title", title).append("author", username)
		.append("body", body).append("permalink", permalink)
		.append("tags", tags)
		.append("comments", Collections.EMPTY_LIST)
		.append("date", new Date());
        
        postsCollection.insertOne(post);


        return permalink;
    }




    // White space to protect the innocent








    // Append a comment to a blog post
    public void addPostComment(final String name, final String email, final String body,
                               final String permalink) {

        // XXX HW 3.3, Work Here
        // Hints:
        // - email is optional and may come in NULL. Check for that.
        // - best solution uses an update command to the database and a suitable
        //   operator to append the comment on to any existing list of comments
    	
    	
    	BasicDBObject comment = new BasicDBObject();
		comment.append("author", name).append("body", body);
		if (!StringUtils.isBlank(email)) {
			comment.append("email", email);
		}

		QueryBuilder query = QueryBuilder.start("permalink").is(permalink);
		postsCollection.updateOne((Bson) query.get(), new BasicDBObject("$push",
				new BasicDBObject("comments", comment)));
		
    }
}
