import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void createMap_andAddTwoItems_ofSameValue_returnsSizeOf_1() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.put("abc", "def"));
        assertEquals(1, map.size());
        Object previous = map.put("abc", "def");
        assertNotNull(previous);

        assertEquals("def", previous);
        assertEquals(1, map.size());
    }

    @Test
    public void createMap_andAddTwoItems_returnsSizeOf_2() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.put("abc", "def"));
        assertNull(map.put("def", "ghi"));
        assertEquals(2, map.size());
    }

    @Test
    public void createMap_withNoValues_onGetKey_abc_returns_null() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.get("abc"));
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
    public void onCreatingMapOfKeyValue_String_String_onAddingEntryOf_Integer_Integer_throw_IllegalArgumentException() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.put("abc", "def"));
        assertEquals(1, map.size());
        assertThrows(IllegalArgumentException.class, ()-> map.put(10, 10));
    }

    @Test
    public void createMap_addKeyValue_123_456_on_put_123_789_returnsPreviousValue_456_andUpdatesKey_123_to_789() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertNull(map.put("123", 456));
        Object value = map.get("123");
        assertEquals(value, 456);

        Object updatedValue = map.put("123", 789);
        assertNotNull(updatedValue);
        assertEquals(789, updatedValue);
        assertNotEquals(value, updatedValue);
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
    public void onAdding_13Items_withLoadFactorOf_point_75_returns_MapSizeOf_23_andCorrectlyRehashedValues() {
        CustomMap map = new CustomMap(Integer.class, Integer.class);
        assertEquals(17, map.getMapSize());
        for (int i = 0; i < 13; i++) {
            map.put(i, i * 10);
        }
        assertEquals(23, map.getMapSize());
        for(int i = 0; i < 7; i++) {
            assertEquals(i * 10, map.get(i));
        }
    }

    @Test
    public void onAdding_13Items_withLoadFactorOf_point_75_returns_MapSizeOf_23_andOnReducingMapToSizeOf_5_reducesMapSizeTo_17() {
        CustomMap map = new CustomMap(Integer.class, Integer.class);
        assertEquals(17, map.getMapSize());
        for(int i = 1; i < 14; i++) {
            map.put(i, i * 10);
        }
        assertEquals(23, map.getMapSize());
        assertEquals(13, map.size());

        for(int i = 1; i < 9; i++) {
            map.remove(i);
        }

        assertEquals(5, map.size());
        assertEquals(17, map.getMapSize());

        for(int i = 10; i < 14; i++) {
            assertEquals(i * 10, map.get(i));
        }
        System.out.println(map.size());
    }
}