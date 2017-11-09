import * as React from 'react'
import { Dropdown, Menu } from 'semantic-ui-react'

interface Dataset {
    id: string,
    name: string
}

interface OwnProps {
    datasets: Dataset[],
    selectedDataset?: string,
    onDatasetSelected?(id: String): void
}

type Props = OwnProps

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

        const { datasets, selectedDataset } = this.props

        const dataset = selectedDataset && datasets.find(d => d.id === selectedDataset)
        return (
            <Menu vertical={false}>
                <Dropdown item={true} text={dataset ? dataset.name : 'Select dataset'}>
                    <Dropdown.Menu>
                        {datasets.map(d => <Dropdown.Item key={d.id}>{d.name}</Dropdown.Item>)}
                    </Dropdown.Menu>
                </Dropdown>
            </Menu>
        )
    }
}

export default DatasetSelector