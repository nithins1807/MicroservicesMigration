@Test
public void shouldDecodeEncodedSearchTermForEntitySearch() throws Exception {
    String searchTerm = "SG3s@%$34 %++,-/;:<=>^|";
    String encodedSearchTerm = "SG3s%40%25%2434%20%25%2B%2B%2C-%2F%3B%3A%3C%3D%3E%5E%7C";

    when(entityService.search(searchTerm, IdType.ALL))
            .thenReturn(Collections.emptyList());

    this.mockMvc.perform(get("/api/entities/search")
                    .header("X-Search-Term", encodedSearchTerm)
                    .header("X-Search-Type", "ALL"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("[]")));

    verify(entityService).search(searchTerm, IdType.ALL);
}
