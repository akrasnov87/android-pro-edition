/*package ru.mobnius.cic.data.manager;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import ru.mobnius.core.utils.DateUtil;
import ru.mobnius.cic.DbGenerate;
import ru.mobnius.cic.ui.model.SortItem;

import static org.junit.Assert.assertEquals;

public class SortManagerTest extends DbGenerate {

    private static final String FIELD1 = "name";
    private static final String FIELD2 = "age";

    private final String mKey = "test";
    private PeopleFilterManager mSortManager;
    private SortItem mSortItem;
    private List<People> mList;

    @Before
    public void setUp() {
        mList = new ArrayList<>();
        mList.add(new People("Саша", 32, true, 10000));
        mList.add(new People("Кирилл", 29, true, 8000));
        mList.add(new People("Маша", 27, false, 6000));
        mList.add(new People("Саша", 33, true, 12000));
        mList.add(new People("Саша", 35, true, 5000));
        mList.add(new People("Дима", 30, true, 4500));
        mList.add(new People("Игорь", 33, true, 8500));

        mSortManager = new PeopleFilterManager(mKey);
        // сортировка по имени против алфавита и по возврасту вверх
        mSortItem = new SortItem(FIELD1, SortItem.DESC);
        mSortManager.addItem(mSortItem);
        mSortManager.addItem(new SortItem(FIELD2));
    }

    @Test
    public void addFilterItem() {
        assertEquals(mSortManager.getItems().length, 2);
    }

    @Test
    public void removeFilterItem() {
        mSortManager.removeItem(mSortItem);
        assertEquals(mSortManager.getItems().length, 1);
        assertEquals(mSortManager.getItems()[0].getName(), FIELD2);
    }

    @Test
    public void updateFilterItem() {
        mSortManager.updateItem(FIELD1, SortItem.ASC);

        SortItem sortItem = mSortManager.getItem(FIELD1);
        assertEquals(sortItem.getType(), SortItem.ASC);
    }

    @Test
    public void toSorters() {
        People[] people = mList.toArray(new People[0]);
        People[] results = mSortManager.toSorters(people);
        assertEquals(results[0].name , "Саша");
        assertEquals(results[6].name , "Дима");

        mSortManager.removeItem(mSortManager.getItem(FIELD1));
        mSortManager.removeItem(mSortManager.getItem(FIELD2));

        mSortManager.addItem(new SortItem("money"));
        results = mSortManager.toSorters(people);
        assertEquals(results[0].name, "Дима");
        assertEquals(results[6].name, "Саша");
    }

    @Test
    public void serialize() throws ParseException {
        String txt = mSortManager.serialize();
        assertEquals(txt, "{\"mDate\":\""+ DateUtil.convertDateToString(mSortManager.getDate()) +"\",\"mItems\":[{\"mName\":\"name\",\"mType\":1},{\"mName\":\"age\",\"mType\":2}],\"mKey\":\"test\"}");
    }

    @Test
    public void deSerialize() {
        String txt = mSortManager.serialize();
        PeopleFilterManager trackingSortManager = new PeopleFilterManager(mKey);
        trackingSortManager.deSerialize(txt);
        assertEquals(trackingSortManager.getItems().length, 2);
    }

    @Test
    public void setFilter() {
        MobniusPreferencesManager.createInstance(getContext(), "login");
        MobniusPreferencesManager preferencesManager = MobniusPreferencesManager.getInstance();
        preferencesManager.clear();
        String txt = mSortManager.serialize();
        preferencesManager.setSort(mKey, txt);

        String result = preferencesManager.getSort(mKey);
        assertEquals(txt, result);
    }

    static class PeopleFilterManager extends SortManager<People> {

        public PeopleFilterManager(String key) {
            super(key);
        }
    }

    static class People {
        public People() {}
        public People(String name, int age, boolean male, double money) {
            this.name = name;
            this.age = age;
            this.isMale = male;
            this.money = money;
        }
        public String name;
        public int age;
        public boolean isMale;
        public double money;
    }
}*/