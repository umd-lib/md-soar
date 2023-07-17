# MD-SOAR Customizations

## Introduction

This document provides information and links to the customizations made to
the stock DSpace code for MD-SOAR.

## Major Customizations

Major customizations have their own documents:

* [DataCiteDOI.md](./DataCiteDOI.md)
* [SolrAutosuggest.md](./SolrAutosuggest.md)
* [SubmissionForm.md](./SubmissionForm.md)
* Community Themes - See the [umd-lib/mdsoar-angular](https://github.com/umd-lib/mdsoar-angular)
  documentation
* [DataCiteDOI.md](./DataCiteDOI.md)
* [SimpleItemPage.md](./SimpleItemPage.md)
* [SubmissionForm.md](./SubmissionForm.md)

## Minor Customizations

### dspace/config/local.cfg.EXAMPLE

The following settings were added to the "dspace/config/local.cfg.EXAMPLE" file,
to override default settings in the stock DSpace configuration files.

* `eperson.subscription.onlynew` - Only send subscription emails for new items

* `usage-statistics.logBots` - Disable logging of spiders/bots in Solr
  statistics.

* `usage-statistics.authorization.admin.usage` - Do not show "Statistics" menu
  entry in the navbar for non-admins users.

* Modified `webui.browse.index.<n>` entries, adding the ability to browse by
  "Type".

* `webui.user.assumelogin` - Enabled administrators to impersonate non-admin
    users

### Email Templates

The email templates in the "dspace/config/emails/" directory were modified to
replace "DSpace" with "MD-SOAR".

### Google Analytics

MD-SOAR uses stock DSpace Google Analytics 4 functionality to track site usage,
including file/bitstream downloads (see
<https://wiki.lyrasis.org/display/DSDOC7x/DSpace+Google+Analytics+Statistics>).

In order to track file downloads, the following properties must be set in
the "dspace/config/local.cfg" file:

* google.analytics.key
* google.analytics.buffer
* google.analytics.cron
* google.analytics.api-secret
