# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## 35 - 2022-12-13

Set job_token as query param instead of as header.

## 34 - 2022-12-23

Add Job-Token to headers if `CI_JOB_TOKEN` is set.

## 33 - 2022-12-23

Find git revision also when not on a branch.
Fix locally set SNAPSHOT versions that now go from e.g. 1-SNAPSHOT to 1 instead of 2 as before.

## 32 - 2022-04-03

Handle the case when not on a branch.

## 31 - 2022-04-02

No longer requires git binary.
Find default branch name instead of hard coded `master`.

## 30 - 2022-04-01

3PP versions bumped.

## 27 - 2021-10-30

3PP versions bumped.

## 23 - 2020-05-16

- Always write the nextversion.properties file

## 22 - 2020-05-09

- Changed activation criteria
- Create target directory if not exist

## 21 - 2020-05-08

Also write the GAV to the properties file.

## 20 - 2020-05-07

Better error messages if version element is missing.

## 19 - 2020-04-24

Now only activated by invoking the plugin with the goal "run".
No longer starts when the prescence of env variables or certain other goals.

## [17] - 2020-04-13

### Added

- Tries to detect if version is bumped manually and increments and uses that instead of remote.

## [10] - 2020-02-15

- Tries to be "CI build aware"
- Takes no action if goals contains "deploy:deploy"
- Adds branch name to version string if on a branch

## [8] - 2020-02-07

- Now adds a property nextversion.commit to the deployed pom
