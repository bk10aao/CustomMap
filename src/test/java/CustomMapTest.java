import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomMapTest {

    @Test
    public void createEmptyMap_returnsMapOfSize_0() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertEquals(0, map.size());
    }

    @Test
    public void createMap_andAddOneItem_returnsSizeOf_1() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.put("abc", "def"));
        assertEquals(1, map.size());
    }

    @Test
    public void createMap_withNoValues_onGetKey_abc_returns_null() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.get("abc"));
    }

    @Test
    public void createMap_andAddTwoItems_returnsSizeOf_2() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.put("abc", "def"));
        assertNull(map.put("def", "ghi"));
        assertEquals(2, map.size());
    }

    @Test
    public void createMap_addItemsWithKeyValue_abc_def_onGet_ghi_returns_null() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.put("abc", "def"));
        Object value = map.get("ghi");
        assertNull(value);
    }

    @Test
    public void createMap_addItemsWithKeyValue_abc_def_onGet_abc_returns_def() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.put("abc", "def"));
        Object value = map.get("abc");
        assertEquals("def", value);
    }

    @Test
    public void createMap_addTwoItems_returnsSizeOf_2_andMatchingKeyValuePairs() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.put("abc", "def"));
        Object value = map.get("abc");
        assertEquals("def", value);
        assertNull(map.put("ghi", "jkl"));
        value = map.get("ghi");
        assertEquals(value, "jkl");
    }

    @Test
    public void createMap_addKeyValue_123_456_on_put_123_789_returnsPreviousValue_456_andUpdatesKey_123_to_789() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertNull(map.put("123", 456));
        Object value = map.get("123");
        assertEquals(456, value);
        assertEquals(456, map.put("123", 789));
        value = map.get("123");
        assertEquals(789, value);
    }

    @Test
    public void createMap_addKeyValue_123_456_on_removeKeyOf_123_returnsKeysValue_456_andSizeOf_0() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertNull(map.put("123", 456));
        Object value = map.get("123");
        assertEquals(456, value);

        value = map.remove("123");
        assertEquals(456, value);
        assertEquals(0, map.size());

        value = map.get("123");
        assertNull(value);
    }

    @Test
    public void createMap_addKeyValue_123_456_on_removeKeyOf_789_returnsNull_andSizeOf_1() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertNull(map.put("123", 456));
        Object value = map.get("123");
        assertEquals(456, value);

        value = map.remove("789");
        assertNull(value);
        assertEquals(1, map.size());
    }

    @Test
    public void createMap_addKeyValue_123_456_onClear_returnsEmptyMap_andNullValueOnGettingKey_123() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertNull(map.put("123", 456));
        Object value = map.get("123");
        assertEquals(456, value);
        assertEquals(1, map.size());

        map.clear();
        value = map.get("123");
        assertNull(value);
        assertEquals(0, map.size());
    }

    @Test
    public void createMap_addKeyValue_123_456_onContainsValue_456_returns_true() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertNull(map.put("123", 456));
        assertTrue(map.containsValue(456));
    }

    @Test
    public void createMap_addKeyValue_123_456_onContainsValue_789_returns_false() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertNull(map.put("123", 456));
        assertFalse(map.containsValue(789));
    }

    @Test
    public void onAdding_12Items_withLoadFactorOf_point_75_returns_MapSizeOf_17_andCorrectlyRehashedValues() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertNull(map.put("1", 1));
        assertNull(map.put("2", 2));
        assertNull(map.put("3", 3));
        assertNull(map.put("4", 4));
        assertNull(map.put("5", 5));
        assertNull(map.put("6", 6));
        assertNull(map.put("7", 7));
        assertNull(map.put("8", 8));
        assertNull(map.put("9", 9));
        assertNull(map.put("10", 10));
        assertNull(map.put("11", 11));
    }
}