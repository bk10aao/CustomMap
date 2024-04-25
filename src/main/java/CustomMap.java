import java.util.LinkedList;

public class CustomMap<T> {

    private LinkedList<MapEntry>[] map;
    private final T key;
    private final T type;

    private int mapSize;
    private int size = 0;
    private static final double LOAD_FACTOR = 0.75;
    private int primesIndex = 0;

    public CustomMap(T key, T value) {
        this.map = new LinkedList[primes[primesIndex]];
        this.mapSize = primes[0];
        this.key = key;
        this.type = value;
    }

    private CustomMap(T key, T value, int mapSize) {
        this.map = new LinkedList[mapSize];
        this.mapSize = mapSize;
        this.key = key;
        this.type = value;
    }

    public void clear() {
        this.map = new LinkedList[primes[primesIndex]];
        this.mapSize = primes[0];
        this.primesIndex = 0;
        this.size = 0;
    }

    public boolean containsValue(T value) {
        for(LinkedList<MapEntry> entryList : map) {
            if(entryList != null) {
                for(MapEntry entry : entryList) {
                    if(entry.value.equals(value)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public T get(T key) {
        if(!key.getClass().equals(this.key))
            return null;
        int index = Math.abs(key.hashCode()) % mapSize;
        LinkedList<MapEntry> indexedMapEntry = map[index];
        if(indexedMapEntry == null)
            return null;
        for(MapEntry mapEntry : indexedMapEntry) {
            if(mapEntry.key.equals(key)) {
                return mapEntry.value;
            }
        }
        return null;
    }

    //this method is really jsut for testing LOAD_FACTOR
    public int getMapSize() {
        return this.mapSize;
    }

    public T put(T key, T value) {
        if(!key.getClass().equals(this.key) || !value.getClass().equals(this.type)) {
            throw new IllegalArgumentException();
        }
        int index = Math.abs(key.hashCode()) % mapSize;
        LinkedList<MapEntry> indexedEntry = map[index];
        if(indexedEntry == null) {
            addNewIndexEntry(key, value, index);
            if((double)size / (double)mapSize > LOAD_FACTOR)
                expand();
            return null;
        } else {
            T result = updateExistingLinkedList(index, key, value);
            double currentLoad = (double)size / (double)mapSize;
            if(currentLoad > LOAD_FACTOR) {
                expand();
            }
            return result;
        }
    }

    public T remove(T key) {
        int index = Math.abs(key.hashCode()) % mapSize;
        LinkedList<MapEntry> entryLinkedList = map[index];
        if(get(key) != null) {
            System.out.println(1);
        }
        if(entryLinkedList != null) {
            for(int i = 0; i < entryLinkedList.size(); i++) {
                MapEntry entry = entryLinkedList.get(i);
                if(entry.key.equals(key)) {
                    MapEntry removed = entryLinkedList.remove(i);
                    size--;
                    System.out.println("map size: " + mapSize + " entries " + size);// + " current size: " + size + " Load factored: " + mapSize / 4);
                    if(mapSize > 17 && size <= mapSize / 4)
                        reduce();
                    return removed.value;
                }
            }
        }
        return null;
    }

    public int size() {
        return this.size;
    }

    private void addNewIndexEntry(T key, T value, int index) {
        map[index] = new LinkedList<>();
        LinkedList<MapEntry> entryIndex = map[index];
        MapEntry mapEntry = new MapEntry(key, value);
        entryIndex.add(mapEntry);
        map[index] = entryIndex;
        size++;
    }

    private void expand() {
        mapSize = primes[++primesIndex];
        CustomMap<T> newMap = new CustomMap(this.key, this.type, mapSize);
        for (LinkedList<MapEntry> linkedList : map) {
            if (linkedList != null) {
                for(MapEntry entry : linkedList) {
                    newMap.put(entry.key, entry.value);
                }
            }
        }
        map = newMap.map;
    }

    private void reduce() {
        primesIndex = (primesIndex / 2);
        mapSize = primes[primesIndex];
        CustomMap newMap = new CustomMap(this.key, this.type, mapSize);

        for (int i = 0; i < map.length; i++) {
            if (map[i] != null)
                for (MapEntry item : map[i]) {
                    newMap.put(item.key, item.value);
                }
        }
        map = newMap.map;
    }

    private T updateExistingLinkedList(int index, T key, T value) {

        LinkedList<MapEntry> currentEntries = map[index];
        for(int i = 0; i < currentEntries.size(); i++) {
            if (currentEntries.get(i).key.equals(key)) {
                MapEntry previous = currentEntries.get(i);
                currentEntries.get(i).value = value;
                return previous.value;
            }
        }
        map[index].add(new MapEntry(key, value));
        size++;
        return null;
    }

    protected static final int[] primes = { 17, 23, 29, 37, 47, 59, 71, 89, 107, 131, 163, 197, 239, 293, 353, 431, 521, 631, 761, 919,
            1103, 1327, 1597, 1931, 2333, 2801, 3371, 4049, 4861, 5839, 7013, 8419, 10103, 12143, 14591,
            17519, 21023, 25229, 30293, 36353, 43627, 52361, 62851, 75431, 90523, 108631, 130363, 156437,
            187751, 225307, 270371, 324449, 389357, 467237, 560689, 672827, 807403, 968897, 1162687, 1395263,
            1674319, 2009191, 2411033, 2893249, 3471899, 4166287, 4999559, 5999471, 7199369 };

    public class MapEntry {
        T key;
        T value;

        MapEntry(T key, T value) {
            this.key = key;
            this.value = value;
        }
    }
}
//
//    public T put(T key, T value) {
//        if(!key.getClass().equals(this.key) || !value.getClass().equals(this.type)) {
//            throw new IllegalArgumentException();
//        }
//        int index = Math.abs(key.hashCode()) % mapSize;
//        LinkedList<MapEntry> indexedEntry = map[index];
//        if(indexedEntry == null) {
//            addNewIndexEntry(key, value, index);
//            if((double)size / (double)mapSize > LOAD_FACTOR)
//                expand();
//            return null;
//        } else {
//            T result = updateExistingLinkedList(index, key, value);
//            double currentLoad = (double)size / (double)mapSize;
//            if(currentLoad > LOAD_FACTOR) {
//                expand();
//            }
//            return result;
//        }
//
//    }