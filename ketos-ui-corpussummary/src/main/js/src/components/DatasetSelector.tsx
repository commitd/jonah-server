import * as React from 'react'
import AppBar from 'material-ui/AppBar'
import Toolbar from 'material-ui/Toolbar'
import { withStyles, StyleRulesCallback, WithStyles, Theme } from 'material-ui/styles'
import Select from 'material-ui/Select'
import { MenuItem } from 'material-ui/Menu'
import { FormControl } from 'material-ui/Form'
import Typography from 'material-ui/Typography'

const styles: StyleRulesCallback = (theme: Theme) => ({
    root: {
        marginTop: 0,
        width: '100%'
    },
})

interface Dataset {
    id: string,
    name: string
}

interface OwnProps {
    datasets: Dataset[],
    selectedDataset?: string,
    onDatasetSelected?(id: String): void
}
type Props = OwnProps & WithStyles

class DatasetSelector extends React.Component<Props> {

    handleDatasetSelected = (e: React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>) => {
        if (this.props.onDatasetSelected) {
            this.props.onDatasetSelected(e.target.value)
        }
    }

    componentWillMount() {
        this.checkIfSingle(this.props)
    }

    componentWillReceiveProps(nextProps: Props) {
        this.checkIfSingle(nextProps)
    }

    checkIfSingle(props: Props) {
        // If we only have one dataset we auto select that
        if (props.selectedDataset == null
            && (props.datasets != null && props.datasets.length === 1)
            && props.onDatasetSelected != null) {
            props.onDatasetSelected(props.datasets[0].id)
        }
    }

    render() {

        const { classes, datasets, selectedDataset } = this.props

        return (
            < div className={classes.root} >
                <AppBar position="static" color="default">
                    <Toolbar>
                        <Typography type="subheading">Dataset:&nbsp;</Typography>
                        <FormControl>
                            <Select
                                value={selectedDataset || ''}
                                onChange={this.handleDatasetSelected}
                                displayEmpty={true}
                            >
                                <MenuItem value=""><em>Select...</em></MenuItem>
                                {datasets.map(d => <MenuItem key={d.id} value={d.id}>{d.name}</MenuItem>)}
                            </Select>
                        </FormControl>

                    </Toolbar>
                </AppBar>
            </  div >
        )
    }
}

export default withStyles(styles)(DatasetSelector)