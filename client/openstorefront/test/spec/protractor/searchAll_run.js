/* Copyright 2014 Space Dynamics Laboratory - Utah State University Research Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

describe('searchAll_Search entire database', function() {

    // *** varies, depending on what is in the sample database ***
    var totalResults = 60; // Articles present

    it('Global search (all blank) returns ' + totalResults + ' expected current db results', function() {
        // Open the main site
        browser.get(theSite, 4000);

        // Search on ALL entries (null search term)
        element.all(by.css('.btn.btn-primary.pull-right')).get(2).click();

        // Wait for it to sync, a bit slower on the VPN
        browser.driver.sleep(12000);

        // Should return 58 results
        expect(element.all(by.repeater('item in data')).count()).toEqual(totalResults);
    }, 25000);


    // GLOBAL SEARCH FOR WILDCARDS
    function searchAll (searchTerm, searchNum) {
        // Clear the search field
        browser.driver.navigate().back();
        browser.refresh();
        browser.driver.sleep(1000);

        element(by.id('mainSearchBar')).sendKeys(searchTerm, protractor.Key.ENTER);
        browser.driver.sleep(6500);
        expect(element.all(by.repeater('item in data')).count()).toEqual(searchNum);
    }
    searchFor1 = 'VANTAGE';    resNum1 = 2;
    it('Global search for ' + searchFor1 + ' returned ' + resNum1 + ' expected results. ', function () {
        searchAll(searchFor1, resNum1);
    }, 20000);
    searchFor2 = '*VANTAGE';    resNum2 = 3;
    it('Global search for (star)VANTAGE returned ' + resNum2 + ' expected results. ', function () {
        searchAll(searchFor2, resNum2);
    }, 20000);
    searchFor3 = 't*R';    resNum3 = 30;
    it('Global search for t(star)R returned ' + resNum3 + ' expected results. ', function () {
        searchAll(searchFor3, resNum3);
    }, 20000);
    searchFor4 = 'TH??r';    resNum4 = 27;
    it('Global search for TH(qm)(qm)r returned ' + resNum4 + ' expected results. ', function () {
        searchAll(searchFor4, resNum4);
    }, 20000);
    searchFor5 = 't*i?';    resNum5 = 45;
    it('Global search for t(star)i(qm) returned ' + resNum5 + ' expected results. ', function () {
        searchAll(searchFor5, resNum5);
    }, 20000);

    it('for a LONG string with special characters- no errors- parses and gives zero results', function() {
        // Open the main site
        browser.get(theSite);

        // Search on a LONG, special character string
        // WARNING:  ***** Causes SQL injection like errors in the error logs! *****
        var bigEntry = '€β™±≠∞µ∑Ω①↖≤ÿñà—””…<HTML>INSERT INTO<table></table>asdljasdoiewrueowoiupewriuocvxnewrq423523#$%&^$#%@#$^#%$^@#$@!$#%@#^@#$^%#$%%$@#645987@#$$@#~~~```/???/\/\|{}{[][][;';
        element(by.id('mainSearchBar')).sendKeys(bigEntry, protractor.Key.ENTER);

        // Should return 0 results, and NOT time out!
        expect(element.all(by.repeater('item in data')).count()).toEqual(0);

    }, 25000);


});
