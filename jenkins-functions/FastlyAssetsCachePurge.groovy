#!/usr/bin/env groovy

// This Function makes various curl requests to purge fastly cache against production assets

def call() {
    sh """
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=http://assets.matchesfashion.com/js/attraqt/autocomplete_designers-data_en.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=http://assets.matchesfashion.com/js/attraqt/autocomplete_designers-data_fr.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=http://assets.matchesfashion.com/js/attraqt/autocomplete_designers-data_ko.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=http://assets.matchesfashion.com/js/attraqt/autocomplete_designers-data_jp.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=http://assets.matchesfashion.com/js/attraqt/autocomplete_categories-data_en.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=http://assets.matchesfashion.com/js/attraqt/autocomplete_categories-data_fr.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=http://assets.matchesfashion.com/js/attraqt/autocomplete_categories-data_ko.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=http://assets.matchesfashion.com/js/attraqt/autocomplete_categories-data_jp.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=http://assets.matchesfashion.com/js/attraqt/autocomplete_popular-data_en.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=http://assets.matchesfashion.com/js/attraqt/autocomplete_popular-data_fr.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=http://assets.matchesfashion.com/js/attraqt/autocomplete_popular-data_ko.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=http://assets.matchesfashion.com/js/attraqt/autocomplete_popular-data_jp.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=http://assets.matchesfashion.com/js/attraqt/autocomplete_justin-data_en.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=http://assets.matchesfashion.com/js/attraqt/autocomplete_justin-data_fr.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=http://assets.matchesfashion.com/js/attraqt/autocomplete_justin-data_ko.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=http://assets.matchesfashion.com/js/attraqt/autocomplete_justin-data_jp.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=https://assets.matchesfashion.com/js/attraqt/autocomplete_designers-data_en.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=https://assets.matchesfashion.com/js/attraqt/autocomplete_designers-data_fr.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=https://assets.matchesfashion.com/js/attraqt/autocomplete_designers-data_ko.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=https://assets.matchesfashion.com/js/attraqt/autocomplete_designers-data_jp.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=https://assets.matchesfashion.com/js/attraqt/autocomplete_categories-data_en.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=https://assets.matchesfashion.com/js/attraqt/autocomplete_categories-data_fr.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=https://assets.matchesfashion.com/js/attraqt/autocomplete_categories-data_ko.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=https://assets.matchesfashion.com/js/attraqt/autocomplete_categories-data_jp.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=https://assets.matchesfashion.com/js/attraqt/autocomplete_popular-data_en.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=https://assets.matchesfashion.com/js/attraqt/autocomplete_popular-data_fr.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=https://assets.matchesfashion.com/js/attraqt/autocomplete_popular-data_ko.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=https://assets.matchesfashion.com/js/attraqt/autocomplete_popular-data_jp.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=https://assets.matchesfashion.com/js/attraqt/autocomplete_justin-data_en.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=https://assets.matchesfashion.com/js/attraqt/autocomplete_justin-data_fr.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=https://assets.matchesfashion.com/js/attraqt/autocomplete_justin-data_ko.js
        curl -X POST http://apps.matchesfashion.com/cache-purger/api/purgeUrl -d urlPurge=https://assets.matchesfashion.com/js/attraqt/autocomplete_justin-data_jp.js
        """
}