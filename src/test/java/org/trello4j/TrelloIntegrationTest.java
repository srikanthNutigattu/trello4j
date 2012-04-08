package org.trello4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.trello4j.model.Action;
import org.trello4j.model.Board;
import org.trello4j.model.Board.PERMISSION_TYPE;
import org.trello4j.model.Card;
import org.trello4j.model.Member;
import org.trello4j.model.Organization;

public class TrelloIntegrationTest {

	private String key = "23ec668887f03d4c71c7f74fb0ae30a4";

	@Test(expected = TrelloException.class)
	public void missingApiKey_shouldThrowException() {
		new TrelloImpl(null);
	}

	@Test
	public void shouldReturnPublicBoard() {
		// GIVEN
		String boardId = "4d5ea62fd76aa1136000000c"; // ID of Trello Development

		// WHEN
		Board board = new TrelloImpl(key, null).getBoard(boardId);

		// THEN
		assertNotNull("Oops, board is null", board);
		assertEquals("Incorrect board id", boardId, board.getId());
		assertEquals("Incorrect name of board", "Trello Development", board.getName());
		assertEquals("Incorrect organization id", "4e1452614e4b8698470000e0", board.getIdOrganization());
		assertEquals("Incorrect url", "https://trello.com/board/trello-development/4d5ea62fd76aa1136000000c", board.getUrl());
		assertFalse("This should be an open board", board.isClosed());
		assertNotNull(board.getDesc());
		assertNotNull(board.getPrefs());
		assertEquals(PERMISSION_TYPE.PUBLIC, board.getPrefs().getVoting());
	}

	@Test
	public void shouldReturnAction() {
		// GIVEN
		String actionId = "4f7fc98a31f53721037b7bdd";

		// WHEN
		Action action = new TrelloImpl(key, null).getAction(actionId);

		// THEN
		assertNotNull("Oops, action is null", action);
		assertEquals("Incorrect action id", actionId, action.getId());
		assertNotNull("Date not set", action.getDate());
		assertNotNull("idMemberCreator not set", action.getIdMemberCreator());

		assertNotNull("memberCreator not set", action.getMemberCreator());
		assertNotNull("memberCreator.id not set", action.getMemberCreator().getId());
		assertNotNull("memberCreator.username not set", action.getMemberCreator().getUsername());
		assertNotNull("memberCreator.fullName not set", action.getMemberCreator().getFullName());
		assertNotNull("memberCreator.initials not set", action.getMemberCreator().getInitials());

		assertNotNull("data not set", action.getData());
		assertNotNull("data.text not set", action.getData().getText());
		assertNotNull("data.board not set", action.getData().getBoard());
		assertNotNull("data.board.id not set", action.getData().getBoard().getId());
		assertNotNull("data.board.name not set", action.getData().getBoard().getName());

	}

	@Test
	public void shouldReturnOrganization() {
		// GIVEN
		String organizationName = "fogcreek";

		// WHEN
		Organization org = new TrelloImpl(key, null).getOrganization(organizationName);

		// THEN
		assertNotNull("Oops, organization is null", org);
		assertEquals("Incorrect organization name", organizationName, org.getName());
	}

	@Test
	public void shouldReturnMember() {
		// GIVEN
		String username = "joelsoderstrom";

		// WHEN
		Member member = new TrelloImpl(key, null).getMember(username);

		// THEN
		assertNotNull("Oops, member is null", member);
		assertNotNull("Avatar hash not set", member.getAvatarHash());
		assertEquals("Incorrect full name", "Joel Söderström", 	member.getFullName());
		assertNotNull("ID not set", member.getId());
		assertEquals("Invalid count of boards", 0, member.getIdBoards().size());
		assertEquals("Invalid count of organizations", 0, member.getIdOrganizations().size());
		assertEquals("Incorrect initials", "JS", member.getInitials());
		assertNotNull("Status not set", member.getStatus());
		assertEquals("Incorrect URL", "https://trello.com/joelsoderstrom", member.getUrl());
		assertEquals("Incorrect username", username, member.getUsername());
	}

	@Test
	public void shouldReturnBoardsByOrganization() {
		// GIVEN
		String organizationName = "fogcreek";
		String trelloDevBoardId = "4d5ea62fd76aa1136000000c";

		// WHEN
		List<Board> boards = new TrelloImpl(key, null).getBoardsByOrganization(organizationName);

		// THEN
		assertTrue("Organization should have at least one board", boards.size() > 0);
		assertTrue("Organization FogCreek should have Trello Development board", hasBoardWithId(boards, trelloDevBoardId));
	}

	@Test
	public void shouldReturnActionsByBoard() {
		// GIVEN
		String trelloDevBoardId = "4d5ea62fd76aa1136000000c";

		// WHEN
		List<Action> actions = new TrelloImpl(key, null).getActionsByBoard(trelloDevBoardId);

		// THEN
		assertTrue("Board should have at least one action", actions.size() > 0);
		assertEquals("Board id and action.data.board.id should be equal", trelloDevBoardId, actions.get(0).getData().getBoard().getId());
	}

	@Test
	public void shouldReturnCard() {
		// GIVEN
		String cardId = "4f6b93de58843df908f6266a";
		
		// WHEN
		Card card = new TrelloImpl(key, null).getCard(cardId);
		
		// THEN
		assertNotNull("Oops, card is null", card);
		assertEquals("Card id should be equal", cardId, card.getId());
		
		if(!card.getAttachments().isEmpty()) {
			assertNotNull("Attachment should be set", card.getAttachments().get(0).get_id());
		}
	}

	private boolean hasBoardWithId(List<Board> boards, String id) {
		boolean res = false;
		for (Board board : boards) {
			if (board.getId().equals(id)) {
				res = true;
				break;
			}
		}
		return res;
	}
}
