package de.thb.crazyhorseracing.service;

public class RandomNameGenerator {
    private static final String[] ADJECTIVES = {
        "Swift", "Brave", "Silent", "Crimson", "Golden",
        "Shadow", "Iron", "Frozen", "Storm", "Wild",
        "Ancient", "Mystic", "Bright", "Dark", "Lucky",
        "Rapid", "Fierce", "Noble", "Ghost", "Solar"
    };

    private static final String[] NOUNS = {
        "Wolf", "Tiger", "Dragon", "Falcon", "Bear",
        "Fox", "Lion", "Eagle", "Raven", "Panther",
        "Phoenix", "Shark", "Viper", "Wizard", "Knight",
        "Hunter", "Guardian", "Samurai", "Pirate", "Nomad"
    };

    private static final String[] SUFFIXES = {
        "", "", "", "",
        "X", "Prime", "Zero", "One", "Nova",
        "Max", "JR", "Pro", "99", "HD", "EX"
    };

    public static String generateRandomName() {
        String adjective = ADJECTIVES[RandomService.nextInt(ADJECTIVES.length)];
        String noun = NOUNS[RandomService.nextInt(NOUNS.length)];
        String suffix = SUFFIXES[RandomService.nextInt(SUFFIXES.length)];

        StringBuilder name = new StringBuilder();
        name.append(adjective).append(noun);

        // 40% chance to add a number
        if (RandomService.nextDouble() < 0.4) {
            name.append(RandomService.nextInt(100));
        }

        if (!suffix.isEmpty()) {
            name.append(suffix);
        }

        return name.toString();
    }
}
