/**
 * Created by Temi Varghese on 28/07/15.
 */
// Karma configuration
// Generated on Mon Feb 23 2015 15:47:55 GMT+1100 (AEDT)

module.exports = function (config) {
    config.set({

        // base path that will be used to resolve all patterns (eg. files, exclude)
        basePath: '',

        plugins: [
            'karma-chrome-launcher', // comes with karma
            'karma-jasmine',
            'karma-coverage',
            'karma-firefox-launcher',
            'karma-phantomjs-launcher',
            'karma-safari-launcher'
        ],


        // frameworks to use
        // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
        frameworks: ['jasmine'],

        // preprocess matching files before serving them to the browser
        // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
        preprocessors: {
            'web-app/js/*.js': ['coverage']
        },

        // list of files / patterns to load in the browser
        files: [
            'web-app/thirdparty/jquery.1.11.2.min.js',
            'web-app/thirdparty/jquery-ui.min.js',
            'web-app/thirdparty/jquery.cookie.js',
            'web-app/thirdparty/spin.min.v2.0.1.js',
            'web-app/thirdparty/leaflet.v0.7.3.js',
            'web-app/thirdparty/Leaflet.fullscreen.v0.0.2.min.js',
            'web-app/thirdparty/bootstrap.min.js',
            'web-app/thirdparty/bootstrap-slider.js',
            'web-app/thirdparty/jsphylosvg-min.js',
            'web-app/thirdparty/jit.js',
            'web-app/thirdparty/knockout-3.0.0.js',
            'web-app/thirdparty/knockout-sortable.min.js',
            'web-app/thirdparty/jquery.contextMenu.js',
            'web-app/thirdparty/emitter.js',
            'web-app/js/utils.js',
            'web-app/js/Control.Checkbox.js',
            'web-app/js/Control.Legend.js',
            'web-app/js/Control.Loading.js',
            'web-app/js/Control.Select.js',
            'web-app/js/Control.Slider.js',
            'web-app/js/Character.js',
            'web-app/js/PJ.js',
            'web-app/js/Filter.js',
            'web-app/js/Habitat.js',
            'web-app/js/Records.js',
            'web-app/js/Map.js',
            'test/js/dependencies.js',
            'test/js/specs/*.js'
        ],


        // list of files to exclude
        exclude: [],


        // test results reporter to use
        // possible values: 'dots', 'progress'
        // available reporters: https://npmjs.org/browse/keyword/karma-reporter
        reporters: ['progress', 'coverage'],


        // web server port
        port: 9876,


        // enable / disable colors in the output (reporters and logs)
        colors: true,


        // level of logging
        // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
        logLevel: config.LOG_DEBUG,


        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: true,


        // start these browsers
        // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
        browsers: ['PhantomJS', 'Chrome','Firefox'],


        // Continuous Integration mode
        // if true, Karma captures browsers, runs the tests and exits
        singleRun: true
    });
};