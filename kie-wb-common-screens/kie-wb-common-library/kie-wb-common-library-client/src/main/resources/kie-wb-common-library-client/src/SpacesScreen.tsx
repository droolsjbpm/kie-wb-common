import * as React from "react";
import * as AppFormer from "appformer-js";
import {LibraryService} from "@kiegroup-ts-generated/kie-wb-common-library-api-rpc"
import {OrganizationalUnitService} from "@kiegroup-ts-generated/uberfire-structure-api-rpc"
import {OrganizationalUnit, OrganizationalUnitImpl} from "@kiegroup-ts-generated/uberfire-structure-api"
import {WorkspaceProjectContextChangeEvent} from "@kiegroup-ts-generated/uberfire-project-api";
import {AuthenticationService} from "@kiegroup-ts-generated/errai-security-server-rpc";
import {NewSpacePopup} from "./NewSpacePopup";
import {PreferenceBeanServerStore} from "@kiegroup-ts-generated/uberfire-preferences-api-rpc";
import {
    LibraryInternalPreferences as LibraryPreference,
    LibraryInternalPreferencesPortableGeneratedImpl as LibraryPreferencePortable
} from "@kiegroup-ts-generated/kie-wb-common-library-api";

interface Props {
    exposing: (self: () => SpacesScreen) => void;
    organizationalUnitService: OrganizationalUnitService,
    authenticationService: AuthenticationService,
    libraryService: LibraryService,
    preferenceBeanServerStore: PreferenceBeanServerStore;
}

interface State {
    spaces: Array<OrganizationalUnit>;
    newSpacePopupOpen: boolean;
}

export class SpacesScreen extends React.Component<Props, State> {

    constructor(props: Props) {
        super(props);
        this.state = {spaces: [], newSpacePopupOpen: false};
        this.props.exposing(() => this);
    }

    private goToSpace(space: OrganizationalUnitImpl) {
        const newPreference = {
            portablePreference: new LibraryPreferencePortable({
                projectExplorerExpanded: false,
                lastOpenedOrganizationalUnit: space.name
            })
        };

        this.props.preferenceBeanServerStore.save6<LibraryPreference, LibraryPreferencePortable>(newPreference)
            .then(i => {
                AppFormer.fireEvent(AppFormer.marshall(new WorkspaceProjectContextChangeEvent({ou: space})));
                (AppFormer as any).LibraryPlaces.goToLibrary();
            });
    }

    private canCreateSpace() {
        //FIXME: fetch permissions from somewhere
        return true;
    }

    private openNewSpacePopup() {
        this.setState({newSpacePopupOpen: true});
    }

    private closeNewSpacePopup() {
        this.setState({newSpacePopupOpen: false});
    }

    public refreshSpaces() {
        this.props.libraryService.getOrganizationalUnits({}).then(spaces => {
            this.setState({spaces: spaces})
        });
    }

    componentDidMount() {
        this.refreshSpaces();
    }

    render() {
        return <>

        {this.state.newSpacePopupOpen &&
        <NewSpacePopup organizationalUnitService={this.props.organizationalUnitService}
                       authenticationService={this.props.authenticationService}
                       onClose={() => this.closeNewSpacePopup()}/>
        }

        {this.state.spaces.length <= 0 &&
        <EmptySpacesScreen onAddSpace={() => this.openNewSpacePopup()}/>}

        {this.state.spaces.length > 0 &&
        <div className={"library container-fluid"}>
            <div className={"row page-content-kie"}>
                <div className={"toolbar-pf"}>
                    <div className={"toolbar-pf-actions"}>
                        <div className={"toolbar-data-title-kie"}>
                            Spaces
                        </div>
                        <div className={"btn-group toolbar-btn-group-kie"}>
                            {this.canCreateSpace() &&
                            <button className={"btn btn-primary"} onClick={() => this.openNewSpacePopup()}>
                                Add Space
                            </button>
                            }
                        </div>
                    </div>
                </div>
                <div className={"container-fluid container-cards-pf"}>
                    <div className={"row row-cards-pf"}>
                        {this.state.spaces.map(s => <Tile
                                key={(s as OrganizationalUnitImpl).name}
                                space={s as OrganizationalUnitImpl}
                                onSelect={() => this.goToSpace(s as OrganizationalUnitImpl)}
                            />
                        )}
                    </div>
                </div>
            </div>
        </div>}

        </>;
    }
}

function EmptySpacesScreen(props: { onAddSpace: () => void }) {
    return <div className={"library"}>
        <div className={"col-sm-12 blank-slate-pf"}>
            <div className={"blank-slate-pf-icon"}>
                <span className={"pficon pficon pficon-add-circle-o"}/>
            </div>
            <h1>
                {AppFormer.translate("NothingHere", [])}
            </h1>
            <p>
                There are currently no Spaces available for you to view or edit. To get started, create a new Space
            </p>
            <div className={"blank-slate-pf-main-action"}>
                <button className={"btn btn-primary btn-lg"} onClick={() => props.onAddSpace()}>
                    Add Space
                </button>
            </div>
        </div>
    </div>;
}

function Tile(props: { space: OrganizationalUnitImpl, onSelect: () => void }) {
    return <>
    <div className={"col-xs-12 col-sm-6 col-md-4 col-lg-3"}>
        <div className={"card-pf card-pf-view card-pf-view-select card-pf-view-single-select"} onClick={() => props.onSelect()}>
            <div className={"card-pf-body"}>
                <div>
                    <h2 className={"card-pf-title"}> {props.space.name} </h2>
                    <h5>
                        {AppFormer.translate("NumberOfContributors", [props.space.contributors!.length.toString()])}
                    </h5>
                </div>
                <div className={"right"}>
                    <span className={"card-pf-icon-circle"}>
                        {props.space.repositories!.length}
                    </span>
                </div>
            </div>
        </div>
    </div>
    </>;
}