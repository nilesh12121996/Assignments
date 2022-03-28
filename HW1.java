import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/*@Test
public void cacheIsWorking() {
        SongCache cache = new SongCacheImpl();
        cache.recordSongPlays("ID-1", 3);
        cache.recordSongPlays("ID-1", 1);
        cache.recordSongPlays("ID-2", 2);
        cache.recordSongPlays("ID-3", 5);
        assertThat(cache.getPlaysForSong("ID-1"), is(4));
        assertThat(cache.getPlaysForSong("ID-9"), is(-1));
        assertThat(cache.getTopNSongsPlayed(2), contains("ID-3",
        "ID-1"));
        assertThat(cache.getTopNSongsPlayed(0), is(empty()));
        }*/
interface SongCache {
    /**
     * Record number of plays for a song.
     */
    void recordSongPlays(String songId, int
            numPlays);
    /**
     * Fetch the number of plays for a song.
     *
     * @return the number of plays, or -1 if the
    song ID is unknown.
     */
    int getPlaysForSong(String songId);
    /**
     * Return the top N songs played, in descending
     order of number of plays.
     */
    List<String> getTopNSongsPlayed(int n);
}

class SongCacheImpl implements SongCache{
    static ConcurrentHashMap<String, Integer> songPlays;
    static ReentrantLock  re= new ReentrantLock();

    public SongCacheImpl() {
        songPlays=new ConcurrentHashMap<>(10);
    }

    @Override
    public void recordSongPlays(String songId, int numPlays) {
        if(re.tryLock()) {
            re.lock();
            if(songPlays.containsKey(songId))
                songPlays.put(songId, (numPlays+ songPlays.get(songId)));
            else
                songPlays.put(songId, numPlays);
            re.unlock();
        }
    }

    @Override
    public int getPlaysForSong(String songId) {
            return songPlays.getOrDefault(songId, -1);
    }

    @Override
    public List<String> getTopNSongsPlayed(int n) {
        Map<String, Integer> topSongsplay= new HashMap<>();
        int it=0;
        List<String> result= new ArrayList<>(n);
        topSongsplay = songPlays.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e1, LinkedHashMap::new));
        for (String key:topSongsplay.keySet()
             ) {
            if(it<n)
                result.add(key);
            else
                break;
            it++;
        }
        return result;
    }

}

public class HW1{
    public static void main(String[] args) {
        SongCache cache = new SongCacheImpl();
        cache.recordSongPlays("ID-1", 3);
        cache.recordSongPlays("ID-1", 1);
        cache.recordSongPlays("ID-2", 2);
        cache.recordSongPlays("ID-3", 5);
        System.out.println((cache.getPlaysForSong("ID-1"))); // is(4));
        System.out.println((cache.getPlaysForSong("ID-9"))); // is(-1));
        System.out.println(cache.getTopNSongsPlayed(2));// contains("ID-3","ID-1"));
        System.out.println(cache.getTopNSongsPlayed(0));// is(empty()));
    }
}

