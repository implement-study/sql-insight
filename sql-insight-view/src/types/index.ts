export interface FixItem {
    name: string,
    length?: number,
    topOffset?: string | number,
    bottomOffset?: string | number,
    effect?: number,
    dialogComponent?: object,
    downArrow?: boolean,
    upArrow?: boolean,
}


export interface InnodbPageItem {
    length: number,
    name: string,
    desc: string,
    detailComponent?: object,
    detailString?: string

}

