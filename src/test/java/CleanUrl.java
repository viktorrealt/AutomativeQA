/**
 * Created by Ириша on 08.10.2016.
 */
public class CleanUrl {
    private String makeURL(String url) {
        if (!url.contains("www") && (url.contains("http://") || url.contains("https://")))
            return url;
        else if (!url.contains("http://") && url.contains("https://"))
            return url;
        else if (!url.contains("https://") && url.contains("http://"))
            return url;
        else if (url.contains("https://www") || url.contains("http://www"))
            return url;
        else
        {
            StringBuilder sb = new StringBuilder(url);
            sb.insert(0, "http://");
            return sb.toString();
        }
    }

    public String GetCleanUrl(String url)
    {
        String result = this.makeURL(url);
        return result;
    }
}
