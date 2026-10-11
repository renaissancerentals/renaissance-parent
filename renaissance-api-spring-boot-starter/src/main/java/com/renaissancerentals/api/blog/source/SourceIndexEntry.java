package com.renaissancerentals.api.blog.source;

/** One post that is public right now: for the nightly check that nothing was missed. */
public record SourceIndexEntry(String id, int version) {}
