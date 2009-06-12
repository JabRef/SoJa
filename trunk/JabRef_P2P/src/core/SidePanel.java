package core;

import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import javax.swing.event.ChangeEvent;
import model.friend.Friend;
import model.friend.FriendRequestsModel;
import view.friend.FriendRequestsView;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import model.*;
import model.friend.FriendsModel;
import model.friend.MyProfile;
import net.sf.jabref.*;
import net.sf.jabref.plugin.SidePanePlugin;
import net.sf.jabref.external.AccessLinksForEntries;
import net.sf.jabref.gui.*;
import net.sf.jabref.search.SearchExpression;
import util.*;
import util.listener.EntrySelectedListener;
import util.thread.*;
import util.visitor.TagFreqVisitor;
import view.*;
import view.email.*;
import view.SelectedEntriesPanel;
import view.autocomplete.AutoComplete;
import view.friend.EditFriendDialog;
import view.tab.*;

/**
 * @author Thien Rong
 */
public class SidePanel implements SidePanePlugin, ActionHandler, ImageConstants {

    FriendsModel friendsModel;
    MessagesTreeModel messagesModel;
    SubscriptionModel subscriptionModel;
    FriendRequestsModel requestModel;
    MyProfile myProfile;
    TagCloudModel tagCloudModel = new TagCloudModel(); // not stored
    FriendReviewsModel friendReviewsModel = new FriendReviewsModel(); // not stored
    SidePaneComponent comp;
    private JabRefFrame frame;
    private SidePaneManager manager;
    private String name = "P2P Test";
    private NetworkDealer dealer;
    JTabbedPane tabbedPane = new JTabbedPane();
    // chat
    private Map<Friend, IMDialog> imDialogs = new HashMap<Friend, IMDialog>();
    // search panel    
    //private final JTextField txtQuery = new JTextField();
    private final AutoComplete txtQuery = new AutoComplete(new DefaultComboBoxModel(), "Search Peers");
    private JPanel searchPanel = init2SearchPanel();
    /* requestID-> search*/
    private Map<String, ImportInspectionDialog> queryToResultMap = new HashMap<String, ImportInspectionDialog>();

    // tag collector
    TagsCollectorThread tagsCollectorThread = new TagsCollectorThread(120000, 1000, this);
    // profile dialog
    private Map<Friend, ProfileDialog> profileDialogs = new HashMap<Friend, ProfileDialog>();
    List<EntrySelectedListener> listeners = new Vector<EntrySelectedListener>();

    public SidePanel() {
    }

    public TagFreqVisitor getLocalTagFreqVisitor() {
        return FrameUtil.getTagFreqVisitor(frame);
    }

    public void init(final JabRefFrame frame, SidePaneManager manager) {        
        /*        for (int i = 0; i < frame.getTabbedPane().getTabCount(); i++) {
        final BasePanel bp = frame.baseAt(i);
        bp.mainTable.addSelectionListener(new ListEventListener<BibtexEntry>() {

        public void listChanged(ListEvent<BibtexEntry> evt) {
        while (evt.next()) {
        switch (evt.getType()) {
        case ListEvent.DELETE:
        System.out.println("DELETE ");
        break;
        case ListEvent.INSERT:
        System.out.println("INSERT ");
        break;
        case ListEvent.UPDATE:
        System.out.println("UPDATE ");
        break;
        }
        EventList<BibtexEntry> entries = evt.getSourceList();
        System.out.println(evt.getIndex());
        int i = evt.getIndex();
        //for (int i = evt.getBlockStartIndex(); i < evt.getBlockEndIndex(); i++) {
        BibtexEntry entry = entries.get(i);
        EntryEditor ee = bp.getEntryEditor(entry);
        ee.add(new JButton("test"), BorderLayout.EAST);
        for (Component component : ee.getComponents()) {
        System.out.println("component" + component);
        }

        
        System.out.println(entry);
        //} // end for loop
        }// end evt.next
        }
        });

        }
         */

        this.frame = frame;
        this.manager = manager;
        this.addHackListener();

        comp = new SidePaneComponent(manager, GUIGlobals.getIconUrl("right"), name) {

            public String getName() {
                return name;
            }
        };
        comp.setLayout(new BorderLayout());
        try {
            comp.add(new LoginTab(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        comp.setVisible(true);
        /*frame.getTabbedPane().addChangeListener(new ChangeListener() {

        public void stateChanged(ChangeEvent e) {
        System.out.println("TabbedPane count " + ((JTabbedPane) e.getSource()).getTabCount());
        }
        });
        DatabaseChangeListener a = new DatabaseChangeListener() {

        public void databaseChanged(DatabaseChangeEvent e) {
        if (e.getType() == DatabaseChangeEvent.ADDED_ENTRY) {
        System.out.println("added ");
        } else if (e.getType() == DatabaseChangeEvent.CHANGED_ENTRY) {
        System.out.println("changed ");
        } else if (e.getType() == DatabaseChangeEvent.CHANGING_ENTRY) {
        System.out.println("changing ");
        } else if (e.getType() == DatabaseChangeEvent.REMOVED_ENTRY) {
        System.out.println("removed ");
        }
        System.out.println(" entry " + e.getEntry());
        }
        };

        for (int i = 0; i < frame.getTabbedPane().getTabCount(); i++) {
        frame.baseAt(i).database().addDatabaseChangeListener(a);
        final File f = frame.baseAt(i).getFile();
        try {
        Globals.fileUpdateMonitor.addUpdateListener(new FileUpdateListener() {

        public void fileUpdated() {
        System.out.println("file updated " + f);
        }

        public void fileRemoved() {
        System.out.println("file removed " + f);
        }
        }, f);

        } catch (IOException ex) {
        ex.printStackTrace();
        }
        }

        PullChangesThread pullThread = new PullChangesThread(frame, 5000,
        new PullChangedListener() {

        public void fileChanged(BasePanel bp) {
        if (dealer != null) {
        for (BibtexEntry entry : bp.database().getEntries()) {
        // Check if entry is shared to friend
        // TODO faster if check which is smaller and use as outer loop
        for (Friend connectedFriend : dealer.getConnectedFriends()) {
        List<Friend> shareToList = CustomBibtexField.getBibtexShare(entry, friendsModel);
        for (Friend shareToFriend : shareToList) {
        // is sharing to this friend and is connected
        if (connectedFriend.equals(shareToFriend)) {
        try {
        dealer.sendBibtexEntry(shareToFriend,
        entry);
        } catch (IOException ex) {
        ex.printStackTrace();
        }
        break;
        }
        }
        }
        } // for each entry
        } // dealer != null
        }
        });
        pullThread.start();
         */
        printBibtex();
    }

    public SidePaneManager getManager() {
        return manager;
    }

    public SidePaneComponent getSidePaneComponent() {
        return comp;
    }

    public JMenuItem getMenuItem() {
        JMenuItem item = new JMenuItem("P2P Test panel");
        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                manager.show(name);
            }
        });
        return item;
    }

    public String getShortcutKey() {
        return null;
    }

    void loadModels() {
        Store s = new Store(myProfile.getName());
        friendsModel = new FriendsModel(s).load();
        messagesModel = new MessagesTreeModel(s).load();
        subscriptionModel = new SubscriptionModel(s).load();
        requestModel = new FriendRequestsModel(s).load();

        if (friendsModel.isNew()) {
            new NewUserWizard(this).setVisible(true);
        }

    //    DefaultMutableTreeNode friendNode = friendsModel.addGroup("Friends");
    //    DefaultMutableTreeNode colleagueNode = friendsModel.addGroup("Colleagues");
    //    friendsModel.addFriend(new Friend("Smith", "Smith", "127.0.0.1", 5150, 5151), colleagueNode);
    //    friendsModel.addFriend(new Friend("Joe", "Joe", "127.0.0.1", 5152, 5153), friendNode);
    //this.imDialogs = Collections.synchronizedMap(imDialogs);
    }

    public void updateLoginUI(Component panelToRemove) {
        loadModels();
        // start login thread
        new ConnectFriendsThread(this, 1000).start();
        tagsCollectorThread.start();

        // remove self and load friends panel
        comp.remove(panelToRemove);
        SelectedEntriesPanel selectedEntriesPanel = new SelectedEntriesPanel(this, "Actions for Selected Items");
        SplitPanel searchAndSelectedItems = new SplitPanel(selectedEntriesPanel, searchPanel, BorderLayout.NORTH);
        FriendRequestsView requestView = new FriendRequestsView(this);
        //comp.add(searchAndSelectedItems, BorderLayout.NORTH);
        //comp.add(requestView, BorderLayout.NORTH);
        comp.add(new SplitPanel(searchAndSelectedItems, requestView, BorderLayout.NORTH), BorderLayout.NORTH);
        tabbedPane.addTab("Profile", new ImageIcon(Loader.get(PROFILE)), new ProfileTab(this));
        tabbedPane.addTab("Inbox", new ImageIcon(Loader.get(INBOX)), new InboxTab(this));
        tabbedPane.addTab("Subscribed", new ImageIcon(Loader.get(RSS)), new SubscriptionPanel(this, "Subscription"));
        tabbedPane.addTab("Tags", new ImageIcon(Loader.get(TAG)), new TagsTab(this));
        tabbedPane.add("Debug", new TestTab(this));
        comp.add(tabbedPane);
        // adjust height but keep existing width
        Dimension preferredDim = new Dimension(comp.getWidth(),
                (int) comp.getPreferredSize().getHeight());
        comp.setSize(preferredDim);
        tabbedPane.setSize(preferredDim);
        manager.updateView();
    }

    public void performSearch() {
        String query = txtQuery.getText();
        txtQuery.addSearchToHistory();
        final ImportInspectionDialog importer = new ImportInspectionDialog(frame, frame.basePanel(),
                CustomBibtexField.getSearchResultFields(), "Searching peers for " + query, false);
        Util.placeDialog(importer, frame);
        // generate id 1st so can put into map b4 sending
        String requestID = GlobalUID.generate(myProfile.getFUID());
        putQuery(requestID, importer);
        dealer.sendSearchRequest(query, requestID);
        importer.setVisible(true);

        new Thread() {

            public void run() {
                try {
                    Thread.sleep(3000);
                    importer.entryListComplete();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    void handleSubscriptionUpdate(Friend friend, Collection<BibtexEntry> entries) {
        for (BibtexEntry entry : entries) {
            this.getSubscriptionModel().checkExistingSubscription(friend, entry);
        }
    }

    void handleProfileResult(Friend friend, ProfileDetail profileDetail) {
        ProfileDialog pd = getProfileDialog(friend);
        if (pd != null) {
            pd.setProfileDetail(profileDetail);
        }
    }

    private JPanel init2SearchPanel() {
        final ButtonGroup searchScope = new ButtonGroup();
        JPanel panel = new JPanel(new BorderLayout());
        txtQuery.addTextActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        JButton btnInfo = new JButton(new ImageIcon(Loader.get(INFO)));
        btnInfo.setToolTipText("User Guide");
        btnInfo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new NewUserWizard(SidePanel.this).setVisible(true);
            }
        });
        panel.add(new SplitPanel(txtQuery, btnInfo, BorderLayout.EAST), BorderLayout.NORTH);

        JPanel pnlScope = new JPanel(new FlowLayout());
        JRadioButton btnAll = new JRadioButton("All");
        JRadioButton btnMyItems = new JRadioButton("My Items");
        JRadioButton btnMyFriends = new JRadioButton("My Friends");
        searchScope.add(btnAll);
        searchScope.add(btnMyItems);
        searchScope.add(btnMyFriends);

        pnlScope.add(btnAll);
        pnlScope.add(btnMyItems);
        pnlScope.add(btnMyFriends);
        //panel.add(pnlScope);
        //panel.setBorder(BorderFactory.createTitledBorder("Search"));
        return panel;
    }

    public void handleRecvIM(Friend friend, String msg) {
        System.out.println("handling recieve IM");
        IMDialog im = getIMDialog(friend);
        im.appendRx(msg);
        im.setLocationRelativeTo(frame);
        System.out.println(im);
        if (false == im.isVisible()) {
            // seems to cause problem 
            im.setVisible(true);
        }
        im.toFront();
        System.out.println("finish receive IM");
    }

    public synchronized IMDialog getIMDialog(Friend friend) {
        IMDialog im = imDialogs.get(friend);
        if (im == null) {
            im = new IMDialog(friend, this);
            imDialogs.put(friend, im);
        }
        // don't setvisible true here, else might block and won't return
        return im;
    }

    public synchronized ProfileDialog getProfileDialog(Friend friend) {
        ProfileDialog pd = profileDialogs.get(friend);
        if (pd == null) {
            pd = new ProfileDialog(this, friend);
            profileDialogs.put(friend, pd);
        }
        // don't setvisible true here, else might block and won't return
        return pd;
    }

    public void handleRecvBibtexEntry(Friend friend, BibtexMessage msg) {
        msg.setFriend(friend.getFUID());
        if (messagesModel.addMessage(msg)) {
            updateTabTitle(1, "Inbox (" + messagesModel.getCount() + ")");
        }
    }

    Collection<BibtexEntry> handleBrowseRequest(Friend friend) throws IOException {
        Collection<BibtexEntry> entries = FrameUtil.getEntriesSharedToFriend(friend, friendsModel, frame);
        return entries;
    }

    /* TODO remove if current review okay
    void handleReviewUpdate(Friend friend, Collection<BibtexEntry> entries, String requestID) {
    ReviewDialog reviewDialog = queryToReviewMap.get(requestID);
    if (reviewDialog == null) {
    return;
    }

    if (entries == null) {
    reviewDialog.handleUpdate(null, friend.getFUID());
    return;
    }

    boolean contains = false;
    for (BibtexEntry bibtexEntry : entries) {
    if (CustomBibtexField.getBUID(bibtexEntry).equals(reviewDialog.getBUID())) {
    reviewDialog.handleUpdate(bibtexEntry, friend.getFUID());
    contains = true;
    break;
    }
    }
    if (contains == false) {
    reviewDialog.handleUpdate(null, friend.getFUID());
    }
    }

    public void performGetReview(BibtexEntry entry, BasePanel bp) {
    if (myProfile == null) {
    frame.showMessage("Please login first to get review from friends");
    return;
    }
    ReviewDialog reviewDialog = new ReviewDialog(entry, bp);
    Util.placeDialog(reviewDialog, frame);
    // generate id 1st so can put into map b4 sending
    String requestID = GlobalUID.generate(myProfile.getFUID());
    putReview(requestID, reviewDialog);
    dealer.sendReviewRequest(requestID);
    reviewDialog.setVisible(true);
    }*/
    public void handleSearchRequest(Friend friend, String query, String requestID) {
        SearchRuleSet searchRules = new SearchRuleSet();
        Hashtable<String, String> searchOptions = new Hashtable<String, String>();
        searchOptions.put("option", query);

        SearchRule rule1;
        try {
            // this searches specified fields if specified
            rule1 = new SearchExpression(Globals.prefs, searchOptions);
        } catch (Exception ex) {
            // we'll do a search in all fields
            rule1 = new NormSearchRule();
        }
        searchRules.addRule(rule1);

        Collection<BibtexEntry> entries = FrameUtil.getEntriesPassSearch(friend, friendsModel, searchRules, searchOptions, frame);

        try {
            dealer.sendSearchResult(friend.getFUID(), entries, requestID);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void handleSearchResult(Friend friend, Collection<BibtexEntry> entries, String requestID) {
        ImportInspectionDialog dialog = queryToResultMap.get(requestID);
        if (dialog != null) {
            for (BibtexEntry bibtexEntry : entries) {
                dialog.addEntry(bibtexEntry);
            }
        }
    }

    public void handleBrowseResult(Friend friend, Collection<BibtexEntry> entries, String requestID) {
        // call the handleSearch
        handleSearchResult(friend, entries, requestID);
        String FUID = friend.getFUID();
        // update tags cloud
        TagFreqVisitor visitor = new TagFreqVisitor();
        for (BibtexEntry bibtexEntry : entries) {
            visitor.visitEntry(bibtexEntry, null);
        }

        piggyBackTags(FUID, visitor.getTagFreq());
        piggyBackReviews(FUID, entries);
    }

    @Override
    public void handleViewFriend(Friend f) {
        System.out.println("Friend view " + f);
        ProfileDialog d = getProfileDialog(f);
        d.setVisible(true);
        String requestID = GlobalUID.generate(myProfile.getFUID());
        dealer.sendProfileRequest(f.getFUID(), requestID);
    }

    @Override
    public void handleChatFriend(Friend friend) {
        IMDialog im = getIMDialog(friend);
        im.setLocationRelativeTo(frame);
        im.setVisible(true);
    }

    @Override
    public void handleViewTag(String keyword) {
        this.txtQuery.setText("keywords=\"" + keyword + "\"");
        this.performSearch();
        System.out.println("Tag view " + keyword);
    }

    @Override
    public void handleAddBibtexCopy(Friend friend, BibtexEntry entry) {
        // cloned so removed BUID will not affect actual if subscribe later
        BibtexEntry clonedEntry = (BibtexEntry) entry.clone();
        // remove since separate copy, BUID to download files if any
        String BUID = CustomBibtexField.removeBUID(clonedEntry);
        int addBibEntries = this.frame.addBibEntries(Arrays.asList(clonedEntry), "", false);

        // should have be null but just in case
        if (BUID == null) {
            return;
        }

        // download files
        if (addBibEntries > 0) {
            List<FileListEntry> fileListEntries = AccessLinksForEntries.getExternalLinksForEntries(Arrays.asList(clonedEntry));
            for (FileListEntry fileListEntry : fileListEntries) {
                String filePath = fileListEntry.getLink();
                if (filePath.length() > 0) {
                    DownloadRequest request = new DownloadRequest(0L, BUID, filePath);
                    this.dealer.sendDownloadRequest(friend, clonedEntry, request);
                }
            }
        }
    }

    @Override
    public void handlePrepareEmail(Collection<BibtexEntry> entries, String to, String subject, String msg) {
        EmailView v = new EmailView(this, null);
        for (BibtexEntry bibtexEntry : entries) {
            v.addEntryToUpload(bibtexEntry);
        }
        v.addAllFriendTo(to);
        v.setSubject(subject);
        v.setMsg(msg);
        v.pack();
        v.setLocationRelativeTo(null);
        v.setVisible(true);
    }

    @Override
    public void handleSubscribe(Friend friend, BibtexEntry entry) {
        subscriptionModel.updateSubscription(friend, entry);
    }

    @Override
    public void handleAcceptedFriendRequest(Friend friendRequest) {
        if (friendsModel.findFriend(friendRequest.getFUID()) == null) {
            friendsModel.addFriendToDeftGroup(friendRequest);
        }
    }

    @Override
    public void handleFriendRequest(Friend friend) {
        this.getRequestModel().addRequest(friend);
    }

    @Override
    public void handleSendFriendRequest(Friend friend) {
        dealer.sendFriendRequest(friend);
    }

    @Override
    public void handleEditFriend() {
        new EditFriendDialog(this).setVisible(true);
    }

    @Override
    public void handleRemoveFriend(Friend f) {
        friendsModel.removeFriend(f);
    }

    public ImportInspectionDialog putQuery(String key, ImportInspectionDialog value) {
        return queryToResultMap.put(key, value);
    }

    private void updateTabTitle(int index, String newTitle) {
        tabbedPane.setTitleAt(index, newTitle);
        tabbedPane.updateUI();
    }

    /**
     *
     * @param selectedFriend friend to connect to
     * @return true if connection okay
     */
    public void doConnect(Friend selectedFriend, boolean silent) {
        /*if (2 != data.length) {
        frame.showMessage("Invalid Connection String. Should be host:port");
        return;
        }
        int port = -1;
        try {
        port = Integer.parseInt(data[1]);
        } catch (NumberFormatException nfe) {
        }
        if (0 >= port) {
        frame.showMessage("Invalid Port Number. Port must be a positive number instead of " + data[1]);
        return;
        }*/
        try {
            dealer.sendConnect(selectedFriend);
        } catch (UnknownHostException ex) {
            if (!silent) {
                frame.showMessage("Invalid host or port " + selectedFriend.getIp() +
                        ":" + selectedFriend.getPort());
            }
        } catch (IOException ex) {
            if (!silent) {
                frame.showMessage(ex.getMessage());
            }
        } catch (ClassNotFoundException ex) {
            if (!silent) {
                frame.showMessage(ex.getMessage());
            }
        }
    }

    public void printBibtex() {
        BasePanel basePanel = frame.basePanel();
        List<BibtexEntry> list = new ArrayList<BibtexEntry>();
        if (basePanel != null) {
            for (BibtexEntry bibtexEntry : basePanel.database().getEntries()) {
                List<FileListEntry> fileListEntries = AccessLinksForEntries.getExternalLinksForEntries(Arrays.asList(bibtexEntry));
                for (FileListEntry fileListEntry : fileListEntries) {
                    File f = new File(fileListEntry.getLink());
                //System.out.println("xx" + fileListEntry.getLink());
                }
                // test add one
                if (list.isEmpty()) {
                    list.add(bibtexEntry);
                    BibtexEntry bib2 = (BibtexEntry) bibtexEntry.clone();
                    bib2.clearField(BibtexFields.KEY_FIELD);
                    bib2.setField("p2p.test", "hey not duplicate");
                    list.add(bib2);
                }
            }

        //        frame.addBibEntries(list, "Friends' name", false);
        //frame.addImportedEntries(frame.basePanel(), list, "Friends' name", false);
        }
    }

    /**
     * Delegate
     * @param id
     */
    public Friend findFriend(String id) {
        return friendsModel.findFriend(id);
    }

    /**
     * Delegate
     * @param myTags
     */
    public void setMyTags(Map<String, Integer> myTags) {
        tagCloudModel.setMyTags(myTags);
    }

    public void setFriendTag(String friendFUID, Map<String, Integer> friendTags) {
        tagCloudModel.setFriendTag(friendFUID, friendTags);
    }

    public NetworkDealer getDealer() {
        return dealer;
    }

    public JabRefFrame getFrame() {
        return frame;
    }

    public FriendsModel getFriendsModel() {
        return friendsModel;
    }

    public void setDealer(NetworkDealer dealer) {
        this.dealer = dealer;
    }

    public MessagesTreeModel getMessagesModel() {
        return messagesModel;
    }

    public SubscriptionModel getSubscriptionModel() {
        return subscriptionModel;
    }

    public TagCloudModel getTagCloudModel() {
        return tagCloudModel;
    }

    public FriendRequestsModel getRequestModel() {
        return requestModel;
    }

    public MyProfile getMyProfile() {
        return myProfile;
    }

    public void setMyProfile(MyProfile myProfile) {
        this.myProfile = myProfile;
    }

    public boolean addEntrySelectedListener(EntrySelectedListener l) {
        return listeners.add(l);
    }

    public boolean removeEntrySelectedListener(EntrySelectedListener l) {
        return listeners.remove(l);
    }

    void addHackListener() {
        // #hack to listen on which entry change, also add listener to new tabs
        final MyEntryEditor myEditor = new MyEntryEditor(this);
        this.addEntrySelectedListener(new EntrySelectedListener() {

            public void entrySelected(BibtexEntry entry, BasePanel bp) {
                EntryEditor ee = bp.getEntryEditor(entry);
                ee.remove(myEditor);
                myEditor.setEntry(entry, bp);
                ee.add(myEditor, BorderLayout.EAST);
            }
        });

        JTabbedPane frameTab = frame.getTabbedPane();
        final ListEventListener<BibtexEntry> evt = new ListEventListener<BibtexEntry>() {

            public void listChanged(ListEvent<BibtexEntry> evt) {
                BasePanel bp = frame.basePanel();
                BibtexEntry entry = bp.getSelectedEntries()[0];
                for (EntrySelectedListener l : listeners) {
                    l.entrySelected(entry, bp);
                }
            //System.out.println(entry + ",");
            }
        };

        this.addHackListener2(evt);
        frameTab.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                SidePanel.this.addHackListener2(evt);
            }
        });

        friendReviewsModel.addPropertyChangeListener(myEditor);
    }

    void addHackListener2(ListEventListener<BibtexEntry> evt) {
        for (int i = 0; i < frame.getTabbedPane().getTabCount(); i++) {
            final BasePanel bp = frame.baseAt(i);
            //System.out.println("eee " + i + " " + bp);
            final int j = i;
            // #hack to remove old one if exists. throws if don't exists
            try {
                bp.mainTable.getSelected().removeListEventListener(evt);
            } catch (Exception ignored) {
            }
            bp.mainTable.addSelectionListener(evt);
        }
    }

    public String getName() {
        return name;
    }

    public void piggyBackTags(String FUID, Map<String, Integer> tfs) {
        // update tags cloud
        tagCloudModel.setFriendTag(FUID, tfs);
        // reset timer
        tagsCollectorThread.resetTimer(FUID);
    }

    private void piggyBackReviews(String FUID, Collection<BibtexEntry> entries) {
        friendReviewsModel.setEntries(FUID, entries);
    }

    public FriendReviewsModel getFriendReviewsModel() {
        return friendReviewsModel;
    }
}
