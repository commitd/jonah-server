import * as React from 'react'
import AppBar from 'material-ui/AppBar'
import Toolbar from 'material-ui/Toolbar'
import { withStyles, StyleRulesCallback, WithStyles, Theme } from 'material-ui/styles'
import Select from 'material-ui/Select'
import { MenuItem } from 'material-ui/Menu'
import { FormControl } from 'material-ui/Form'

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

    handleDatasetSelected = () => {
        if (this.props.onDatasetSelected) {
            this.props.onDatasetSelected('hello')
        }
    }
    render() {

        const { classes, datasets, selectedDataset } = this.props

        return (
            < div className={classes.root} >
                <AppBar position="static" color="default">
                    <Toolbar>
                        <FormControl >

                            <Select value={selectedDataset}>
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