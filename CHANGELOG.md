# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

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
