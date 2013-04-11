basePath = '../';

files = [
  JASMINE,
  JASMINE_ADAPTER,
  'webapp/lib/angular/angular.js',
  'webapp/lib/angular/angular-*.js',
  'webapp/lib/OpenLayers.js',
  'webapp/lib/jquery/jquery-1.7.2.js',
  'webapp/lib/jquery.ui/jquery.ui.js',
  'webapp/lib/jquery.treeview/jquery.treeview.js',
  'test/lib/angular/angular-mocks.js',
  'webapp/js/*.js',
  'webapp/js/ctrl/*.js',
  'webapp/js/model/*.js',
  'webapp/js/modules/*.js',
  'webapp/js/modules/services/*.js',
  'test/unit/**/*.js'
];

autoWatch = true;

browsers = ['Chrome'];

junitReporter = {
  outputFile: 'test_out/unit.xml',
  suite: 'unit'
};
