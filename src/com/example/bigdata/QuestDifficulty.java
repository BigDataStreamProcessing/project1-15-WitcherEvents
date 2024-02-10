package com.example.bigdata;

import net.datafaker.Faker;

public class QuestDifficulty {
    private static final Faker faker = new Faker();

    static String getQuestDifficulty() {
        return faker.expression("#{options.option 'Easy','Medium','Hard','Very Hard','Lethal'}");
    }
}
